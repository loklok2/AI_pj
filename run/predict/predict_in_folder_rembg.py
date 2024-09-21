import os
import csv
from PIL import Image
import sys
import time
from tqdm import tqdm
import numpy as np  # numpy 추가
from rembg import remove
from io import BytesIO

# 프로젝트 루트 디렉토리를 Python 경로에 추가
sys.path.append(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))

from run.predict.style_predictor_custom import StylePredictor

def count_images(folder):
    count = 0
    for root, _, files in os.walk(folder):
        count += sum(1 for f in files if f.lower().endswith(('.png', '.jpg', '.jpeg', '.bmp', '.gif')))
    return count

def remove_background(image):
    # 이미지를 numpy 배열로 변환
    img_np = np.array(image)

    # rembg로 배경 제거 (이미지를 numpy 형태로 전달)
    img_removed_np = remove(img_np)
    
    # 결과 이미지를 PIL 형식으로 변환
    bg_removed_img = Image.fromarray(img_removed_np)

    # RGBA(4채널)이면 RGB(3채널)로 변환
    if bg_removed_img.mode == 'RGBA':
        bg_removed_img = bg_removed_img.convert('RGB')
    
    return bg_removed_img

def process_images_and_save_csv(root_folder, output_csv, output_folder):
    predictor = StylePredictor()
    results = []

    total_images = count_images(root_folder)
    start_time = time.time()

    print(f"총 {total_images}개의 이미지를 처리합니다.")

    # 배경 제거된 이미지 저장 디렉토리 생성
    os.makedirs(output_folder, exist_ok=True)

    with tqdm(total=total_images, desc="이미지 처리 중", unit="img") as pbar:
        for root, _, files in os.walk(root_folder):
            for file in files:
                if file.lower().endswith(('.png', '.jpg', '.jpeg', '.bmp', '.gif')):
                    img_path = os.path.join(root, file)
                    try:
                        with Image.open(img_path) as img:
                            img = img.convert('RGB')

                            # 배경 제거 수행
                            bg_removed_img = remove_background(img)

                            # 스타일 예측
                            predictions = predictor.predict(bg_removed_img)

                            # 배경 제거 이미지 저장
                            output_img_path = os.path.join(output_folder, f"bg_removed_{file}")
                            bg_removed_img.save(output_img_path, "PNG")

                            # 상대 경로 계산
                            rel_path = os.path.relpath(img_path, root_folder)
                            folder_name = os.path.basename(os.path.dirname(rel_path))

                            # 결과 저장
                            results.append({
                                'image_path': f"{folder_name}/{file}",
                                'bg_removed_path': f"bg_removed_{file}",
                                'style': tuple(pred for pred in predictions[::-1])
                            })

                        pbar.update(1)

                    except Exception as e:
                        print(f"\n이미지 처리 중 오류 발생: {img_path}")
                        print(f"오류 내용: {str(e)}")

    # CSV 파일로 저장
    with open(output_csv, 'w', newline='', encoding='utf-8') as csvfile:
        fieldnames = ['image_path', 'bg_removed_path', 'style']
        writer = csv.DictWriter(csvfile, fieldnames=fieldnames)

        writer.writeheader()
        for result in results:
            writer.writerow(result)

    total_time = time.time() - start_time
    print(f"\n처리 완료: 총 {len(results)}개 이미지, 소요 시간: {total_time:.2f}초")
    print(f"결과가 {output_csv}에 저장되었습니다.")

if __name__ == "__main__": 
    root_folder = "C:/SS/AI_project_data/kfashoin_ai_model/img/크롤링데이터"
    output_csv = "C:/SS/AI_project_data/kfashoin_ai_model/output/output_style.csv"
    output_folder = "C:/SS/AI_project_data/kfashoin_ai_model/output/bg_removed_images"

    # 출력 디렉토리가 없으면 생성
    os.makedirs(os.path.dirname(output_csv), exist_ok=True)

    process_images_and_save_csv(root_folder, output_csv, output_folder)
