import sys
import os
import json
import torch
from torchvision import transforms
from PIL import Image

# 프로젝트 루트 디렉토리를 파이썬 경로에 추가
project_root = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
sys.path.append(project_root)

from utility.resnest import resnest50d
from utility.util import Warp  # Warp 함수를 import

class AttributePredictor:
    def __init__(self, checkpoint_dir='checkpoint', data_dir='data'):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.models = self._load_models(checkpoint_dir)
        self.labels = self._load_labels(data_dir)
        self.image_size = 224  # 또는 모델에 맞는 적절한 크기
        self.transforms = self._create_transforms()
        self.thresholds = {
            'detail': 0.4,
            'texture': 0.2,
            'print': 0.4
        }

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
                Warp(self.image_size),
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
                print(f"{model_name}   output : ", output,  output.shape)
                probabilities = torch.sigmoid(output).squeeze().cpu().tolist()

            if model_name in ['detail', 'texture', 'print']:
                threshold = self.thresholds[model_name]
                predictions = self._process_multi_label_predictions(probabilities, self.labels[model_name], threshold)
            else:
                top3 = sorted(zip(self.labels[model_name], probabilities), key=lambda x: x[1], reverse=True)[:3]
                predictions = [(label, prob) for label, prob in top3]

            results[model_name] = predictions

        return results

    def _process_multi_label_predictions(self, probabilities, labels, threshold=0.5):
        predictions = []
        for prob, label in zip(probabilities, labels):
            if prob > threshold:
                predictions.append((label, prob))
        return sorted(predictions, key=lambda x: x[1], reverse=True)

import time
# 사용 예시
if __name__ == '__main__':
    start_time = time.time()
    predictor = AttributePredictor()
    img_path = 'img/K-fashion_HardSegImgs_aim/48076/48076_\ud558\uc758.jpg'
    image = Image.open(img_path).convert('RGB')
    results = predictor.predict(image)

    for model_name, predictions in results.items():
        print(f"\n{model_name.capitalize()} 예측:")
        for label, prob in predictions:
            print(f'   >>{label}: 확률 {prob*100:.2f} %')
    print(f"소요 시간: {time.time() - start_time:.2f}초")