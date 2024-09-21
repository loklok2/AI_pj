import sys
import os
import json
import torch
from torchvision import transforms
from PIL import Image
import torch.nn as nn
import matplotlib.pyplot as plt
import numpy as np
from rembg import remove
import io

# 프로젝트 루트 디렉토리를 파이썬 경로에 추가
project_root = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
sys.path.append(project_root)
from utility.resnest import resnest50d

class AttributePredictor:
    def __init__(self, checkpoint_dir='checkpoint', data_dir='data'):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.models = self._load_models(checkpoint_dir)
        self.labels = self._load_labels(data_dir)
        self.image_size = 224  # 또는 모델에 맞는 적절한 크기
        self.transforms = self._create_transforms()
        # self.extractor = self.extract_features

    def _load_models(self, checkpoint_dir):
        models = {}
        for model_name in ['category', 'detail', 'texture', 'print']:
            num_classes = {'category': 21, 'detail': 40, 'texture': 27, 'print': 21}[model_name]
            model_path = os.path.join(checkpoint_dir, f'kfashion_{model_name}', f'model_{model_name}_best.pth.tar')
            
            model = resnest50d(pretrained=False, nc=num_classes)
            model = model.to(self.device)
            checkpoint = torch.load(model_path, map_location=self.device, weights_only=True)
            model.load_state_dict(checkpoint['state_dict'])
            model.eval()
            models[model_name] = model
        return models

    def _load_labels(self, data_dir):
        labels = {}
        for model_name in ['category', 'detail', 'texture', 'print']:
            label_path = os.path.join(data_dir, f'kfashion_{model_name}', f'category_{model_name}_final.json')
            with open(label_path, 'r') as f:
                labels[model_name] = list(json.load(f).keys())
        return labels

    def _create_transforms(self):
        transforms_dict = {}
        for model_name, model in self.models.items():
            normalize = transforms.Normalize(mean=model.image_normalization_mean,
                                             std=model.image_normalization_std)
            transforms_dict[model_name] = transforms.Compose([
                transforms.Resize(self.image_size),  # 이미지 크기만 맞춤
                transforms.ToTensor(),
                normalize,
            ])
        return transforms_dict
    
    def preprocess_image(self, image, model_name):
        image = self.transforms[model_name](image)
        return image.unsqueeze(0).to(self.device)

    def predict(self, img_path):
        results = {}

        for model_name, model in self.models.items():
            img_tensor = self.preprocess_image(img_path, model_name)
            with torch.no_grad():
                output = model(img_tensor)
                
                # argsort를 사용하여 상위 3개 인덱스 선택
                top3_indices = output.squeeze().argsort(descending=True)[:3].cpu().numpy()
                
                # 선택된 인덱스에 해당하는 라벨 가져오기
                predictions = [self.labels[model_name][idx] for idx in top3_indices]

            results[model_name] = predictions

        return results

    def extract_features(self, img_path):
        features = {}

        for model_name, model in self.models.items():
            img_tensor = self.preprocess_image(img_path, model_name)
            
            # 특성 추출을 위해 모델의 마지막 층을 제거
            feature_extractor = nn.Sequential(*list(model.children())[:-1])
            feature_extractor.eval()

            with torch.no_grad():
                feature = feature_extractor(img_tensor)
                feature = feature.squeeze().cpu().numpy()
                
            features[model_name] = feature

        return features

    def predict_category(self, img):
        model = self.models['category']
        img_tensor = self.preprocess_image(img, 'category')
        
        with torch.no_grad():
            output = model(img_tensor)
            probabilities = torch.softmax(output, dim=1)
            top3_probs, top3_indices = probabilities.squeeze().topk(3)
            
            predictions = [(self.labels['category'][idx.item()], prob.item()) for idx, prob in zip(top3_indices, top3_probs)]
        
        return predictions
    
# rembg를 통해 배경을 제거하는 함수
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

import time
# 사용 예시
if __name__ == '__main__':
    start_time = time.time()
    predictor = AttributePredictor()
    
    img_path = 'D:/선신/업체제공/18/AFH3PT009.jpg'
    # img_path = 'img/K-fashion_HardSegImgs_aim/59528/59528_상의.jpg'
    image = Image.open(img_path).convert('RGB')
    
    # 배경 제거 수행
    # image = remove_background(image)
    
    # 이미지 시각화
    img_np = np.array(image)
    plt.imshow(img_np)

    # 배경이 제거된 이미지로 예측 수행
    results = predictor.predict(image)

    # 예측 결과 출력
    for model_name, predictions in results.items():
        print(f"\n{model_name.capitalize()} 예측:")
        for i, label in enumerate(predictions, 1):
            print(f'   {i}. {label}')
    
    print(f"소요 시간: {time.time() - start_time:.2f}초")
    plt.show()
