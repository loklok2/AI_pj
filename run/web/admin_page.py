from transformers import SegformerImageProcessor, AutoModelForSemanticSegmentation
from PIL import Image
import matplotlib.pyplot as plt
import torch.nn as nn
import numpy as np
import torch
import sys
import os
from tqdm import tqdm  # tqdm 모듈 추가

project_root = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
sys.path.append(project_root)


from run.predict.attribute_predictor_custom import AttributePredictor
from db.faiss_attribute_cos import MultiFeatureStyleSearcher
from run.extract_attribute_vect import extract_category_features_to_csv
from run.crawling.naver import CrawlerThread

processor = SegformerImageProcessor.from_pretrained("mattmdjaga/segformer_b2_clothes")
model = AutoModelForSemanticSegmentation.from_pretrained("mattmdjaga/segformer_b2_clothes")
predictor = AttributePredictor()
searcher = MultiFeatureStyleSearcher()

def crawl_image(keyword, save_folder, item_nums):
    keyword = keyword[0]
    crawler_thread = CrawlerThread(keyword, save_folder, item_nums)
    crawler_thread.start()
    crawler_thread.join()  # 스레드가 완료될 때까지 기다림

    # 스레드 실행이 완료되면 df를 반환
    return crawler_thread.df  # df를 반환

def process_image(image, processor, model, desired_labels):
    # 이미지가 RGB 형식이 아닐 경우 RGB로 변환
    if image.mode != 'RGB':
        image = image.convert('RGB')

    # 이미지 전처리
    inputs = processor(images=image, return_tensors="pt")

    # 모델 예측
    outputs = model(**inputs)
    logits = outputs.logits.cpu()

    # 이미지 크기에 맞게 로짓스 크기 조정
    upsampled_logits = nn.functional.interpolate(
        logits,
        size=image.size[::-1],
        mode="bilinear",
        align_corners=False,
    )

    # 예측된 segmentation 맵 생성
    pred_seg = upsampled_logits.argmax(dim=1)[0].numpy()

    # 결과를 저장할 리스트
    processed_images = []
    
    clothes_labels = {
        "가디건": 4,       # Upper-clothes
        "니트웨어": 4,         # Upper-clothes
        "드레스": 7,       # Dress
        "래깅스": 6,       # Pants
        "베스트": 4,     # Upper-clothes
        "브라탑": 4, # Upper-clothes
        "블라우스": 4,         # Upper-clothes
        "셔츠": 4,         # Upper-clothes
        "스커트": 5,       # Skirt
        "재킷": 4,         # Upper-clothes
        "점퍼": 4,         # Upper-clothes
        "점프수트": 7,         # Upper-clothes
        "조거팬츠": 6,         # Pants 
        "짚업": 4,         # Upper-clothes
        "청바지": 6,         # Pants 
        "코트": 4,         # Upper-clothes
        "탑": 4,         # Upper-clothes
        "티셔츠": 4,         # Upper-clothes
        "패딩": 4,         # Upper-clothes
        "팬츠": 6,         # Pants 
        "후드티": 4,         # Upper-clothes
    }
    # 각각의 옷 부분을 처리
    for label in desired_labels:
        # 해당 옷 레이블에 맞는 마스크 생성
        cloth_mask = pred_seg == clothes_labels[label]  # 수정된 부분

        # 원본 이미지에 해당하는 옷 부분만 남기고 나머지는 검정색 처리
        cloth_image = np.array(image).copy()
        cloth_image[~cloth_mask] = 0

        # PIL 이미지로 변환
        output_image = Image.fromarray(cloth_image)

        # 결과를 리스트에 추가
        processed_images.append((label, output_image))

    return processed_images

def process_and_save_images(input_folder, output_folder, desired_labels):

    # 출력 폴더가 없으면 생성
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # 카테고리와 모델 레이블 매핑

    # 이미지 처리 및 저장
    for filename in tqdm(os.listdir(input_folder), desc="Processing Images"):
        if filename.endswith(('.png', '.jpg', '.jpeg')):  # 이미지 파일 형식 확인
            image_path = os.path.join(input_folder, filename)
            image = Image.open(image_path)

            # 이미지 처리 메서드 호출
            processed_images = process_image(image, processor, model, desired_labels)

            # 처리된 이미지를 저장
            for label, output_image in processed_images:
                output_image_path = os.path.join(output_folder, f"{label}_{filename}")
                output_image.save(output_image_path)


if __name__ == '__main__':
    crawl_folder = "img/crawl"
    output_folder = os.path.join(crawl_folder, "processed_images")
    category = ["가디건"]

    crawl_image(category, crawl_folder)
    process_and_save_images(crawl_folder, output_folder, category)
    extract_category_features_to_csv(output_folder, output_folder)
    csv_paths = {
    'category': os.path.join(output_folder, 'category_features.csv'),
    'texture': os.path.join(output_folder, 'texture_features.csv'),
    'print': os.path.join(output_folder, 'print_features.csv'),
    'details': os.path.join(output_folder, 'detail_features.csv')
    }
    attribute_searcher = MultiFeatureStyleSearcher(csv_paths)
    img_path = "img/이미지인덱싱/2.jpg"
    img = Image.open(img_path)
    processed_images = process_image(img, processor, model, category)
    json_result = attribute_searcher.get_similar_images_json(img)
    result = predictor.predict(img)
    for feature_type, thumbnails in result.items():
        print(f"{feature_type.capitalize()} Results:")
        for idx, thumbnail in enumerate(thumbnails, 1):
            print(f"{idx}. {thumbnail}")
        print()  # 각 특성 결과 후에 빈 줄 추가