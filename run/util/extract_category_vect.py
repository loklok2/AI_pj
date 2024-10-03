import os
import pandas as pd
import torch
from tqdm import tqdm
from PIL import Image
from predict.attribute_predictor_custom import AttributePredictor

def extract_category_features_to_csv(image_folder, output_csv):
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"사용 중인 장치: {device}")

    predictor = AttributePredictor()

    all_features = []
    all_image_files = []

    for root, dirs, files in os.walk(image_folder):
        for img_file in files:
            if img_file.lower().endswith(('.jpg', '.png', '.jpeg')):
                all_image_files.append(os.path.join(root, img_file))

    for img_path in tqdm(all_image_files, desc="이미지 처리 중"):
        image = Image.open(img_path).convert('RGB')
        
        # 카테고리 특성 추출
        category_features = predictor.extract_category_features(image)

        # 카테고리 예측
        category_predictions = predictor.predict_category(image)

        # 폴더명과 파일이름 결합
        relative_path = os.path.relpath(img_path, image_folder)
        file_id = os.path.join(os.path.dirname(relative_path), os.path.basename(img_path))

        # 특성과 예측 결과 결합
        combined_features = [file_id] + category_features.tolist() + [pred[0] for pred in category_predictions] + [pred[1] for pred in category_predictions]
        all_features.append(combined_features)

    # 데이터프레임 생성
    columns = ['file_id'] + [f'feature_{i}' for i in range(len(category_features))] + ['category1', 'category2', 'category3', 'prob1', 'prob2', 'prob3']
    df = pd.DataFrame(all_features, columns=columns)

    # CSV 파일로 저장
    df.to_csv(output_csv, index=False)
    print(f"카테고리 특성이 {output_csv}에 저장되었습니다.")

if __name__ == '__main__':
    image_folder = 'img\크롤링 데이터'
    output_csv = 'output/image_category_features.csv'
    
    extract_category_features_to_csv(image_folder, output_csv)