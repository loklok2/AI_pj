import os
import pandas as pd
import torch
from tqdm import tqdm
from PIL import Image
from predict.style_predictor_custom import StylePredictor
import numpy as np

def extract_features_to_csv(image_folder, output_csv, batch_size=300):
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"사용 중인 장치: {device}")

    predictor = StylePredictor()
    predictor.model = predictor.model.to(device)

    all_image_files = []
    for root, dirs, files in os.walk(image_folder):
        for img_file in files:
            if img_file.lower().endswith(('.jpg', '.png', '.jpeg')):
                all_image_files.append(os.path.join(root, img_file))

    total_images = len(all_image_files)
    batch_count = (total_images + batch_size - 1) // batch_size

    unique_features = {}
    removed_images = []

    for batch_index in range(batch_count):
        start_idx = batch_index * batch_size
        end_idx = min((batch_index + 1) * batch_size, total_images)
        batch_files = all_image_files[start_idx:end_idx]

        for img_path in tqdm(batch_files, desc=f"배치 {batch_index + 1}/{batch_count} 처리 중"):
            image = Image.open(img_path).convert('RGB')
            feature = predictor.style_extractor(image)
            feature_key = hash(feature.tobytes())
            
            if feature_key not in unique_features:
                styles = predictor.predict(image) 
                relative_path = os.path.relpath(img_path, image_folder)
                file_id = os.path.join(os.path.dirname(relative_path), os.path.basename(img_path))
                combined_features = [file_id] + feature.tolist() + styles
                unique_features[feature_key] = {'features': combined_features, 'original': file_id}
            else:
                original_file = unique_features[feature_key]['original']
                removed_images.append((img_path, original_file))

        print(f"배치 {batch_index + 1} 처리 완료: 총 {len(batch_files)}개의 이미지 중 {len(unique_features)}개의 고유한 이미지가 처리되었습니다.")

    all_features = [item['features'] for item in unique_features.values()]

    columns = ['file_id'] + [f'feature_{i}' for i in range(len(feature))] + ['style1', 'style2', 'style3']
    df = pd.DataFrame(all_features, columns=columns)

    df.to_csv(output_csv, index=False)
    print(f"모든 특성이 {output_csv}에 저장되었습니다.")
    print(f"총 {total_images}개의 이미지 중 {len(all_features)}개의 고유한 이미지가 처리되었습니다.")

    if removed_images:
        print(f"\n제거된 중복 이미지 파일:")
        for removed, original in removed_images:
            print(f"{os.path.basename(removed)} (원본: {os.path.basename(original)})")

        removed_csv = f"{os.path.splitext(output_csv)[0]}_removed.csv"
        pd.DataFrame(removed_images, columns=['removed_image_path', 'original_image_path']).to_csv(removed_csv, index=False)
        print(f"제거된 이미지 목록이 {removed_csv}에 저장되었습니다.")

if __name__ == '__main__':
    image_folder = 'img\이미지인덱싱'
    output_csv = 'output/image_features(2).csv'
    
    extract_features_to_csv(image_folder, output_csv)