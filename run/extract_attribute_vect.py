import os
import pandas as pd
import torch
from tqdm import tqdm
from PIL import Image
from predict.attribute_predictor_custom import AttributePredictor

def extract_category_features_to_csv(image_folder, output_folder):
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"사용 중인 장치: {device}")

    predictor = AttributePredictor()

    # 각 모델별 CSV 파일 데이터를 저장할 딕셔너리
    model_data = {}

    all_image_files = []

    # 이미지 파일을 수집합니다.
    for root, dirs, files in os.walk(image_folder):
        for img_file in files:
            if img_file.lower().endswith(('.jpg', '.png', '.jpeg')):
                all_image_files.append(os.path.join(root, img_file))

    # 모든 이미지를 처리합니다.
    for img_path in tqdm(all_image_files, desc="이미지 처리 중"):
        image = Image.open(img_path).convert('RGB')
        
        # 카테고리 특성 추출 (딕셔너리 형태로 model_name -> features 반환)
        category_features_dict = predictor.extract_features(image)

        # 폴더명과 파일이름 결합
        relative_path = os.path.relpath(img_path, image_folder)
        file_id = os.path.join(os.path.dirname(relative_path), os.path.basename(img_path))

        # 각 모델별로 데이터를 처리합니다.
        for model_name, features in category_features_dict.items():
            # 특성 데이터만 결합
            combined_features = [file_id] + features.tolist()

            # combined_features와 columns의 길이를 비교하는 검증 코드 추가
            expected_columns_len = 1 + len(features)
            actual_data_len = len(combined_features)
            
            # 길이가 다르면 오류를 출력하고 어떤 파일에서 문제인지 확인
            if actual_data_len != expected_columns_len:
                print(f"길이 불일치: model_name = {model_name}, file_id = {file_id}")
                print(f"combined_features 길이: {actual_data_len}, 예상 컬럼 길이: {expected_columns_len}")

            # 모델별로 데이터 저장을 위한 구조 생성
            if model_name not in model_data:
                # 모델별 컬럼명 설정
                columns = ['file_id'] + [f'feature_{i}' for i in range(len(features))]
                model_data[model_name] = {
                    "columns": columns,
                    "data": []
                }

            # 모델에 해당하는 데이터를 추가
            model_data[model_name]["data"].append(combined_features)

    # 각 모델별로 데이터를 CSV 파일로 저장합니다.
    for model_name, model_info in model_data.items():
        output_csv = os.path.join(output_folder, f"{model_name}_features.csv")
        df = pd.DataFrame(model_info["data"], columns=model_info["columns"])
        df.to_csv(output_csv, index=False)
        print(f"{model_name} 모델의 특성이 {output_csv}에 저장되었습니다.")

if __name__ == '__main__':
    image_folder = 'img/이미지인덱싱'  # 이미지가 있는 폴더 경로
    output_folder = 'output/db'  # CSV 파일을 저장할 폴더 경로

    # 출력 폴더가 없으면 생성
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    extract_category_features_to_csv(image_folder, output_folder)
