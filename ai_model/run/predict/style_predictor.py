import torch
from torchvision import transforms
from PIL import Image
import pickle
import os
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
from utility.ml_gcn import gcn_resnet101
from utility.util import Warp 

#워닝 무시
import warnings
warnings.filterwarnings("ignore", category=UserWarning)

class StylePredictor:
    def __init__(self, 
                 model_path='checkpoint/kfashion_style/model_best.pth.tar',
                 adj_path='data/kfashion_style/custom_adj_final.pkl',
                 word_embedding_path='data/kfashion_style/custom_glove_word2vec_final.pkl',
                 num_classes=10, 
                 t=0.03):
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
        self.model = self._load_model(model_path, adj_path, num_classes, t)
        self.word_embeddings = self._load_word_embeddings(word_embedding_path)
        self.image_size = 224  # 또는 모델에 맞는 적절한 크기
        self.transform = self._create_transform()
        self.style_dict = {0: "클래식", 1: "매니시", 2: "페미니", 3: "에스닉", 4: "컨템포러리", 
                           5: "내추럴", 6: "젠더리스", 7: "스포티", 8: "서브컬처", 9: "캐주얼"}

    def _load_model(self, model_path, adj_path, num_classes, t):
        model = gcn_resnet101(num_classes=num_classes, t=t, adj_file=adj_path)
        model = model.to(self.device)
        checkpoint = torch.load(model_path, map_location=self.device, weights_only=True)
        model.load_state_dict(checkpoint['state_dict'])
        model.eval()
        return model

    def _load_word_embeddings(self, path):
        if not os.path.exists(path):
            raise FileNotFoundError(f"단어 임베딩 파일을 찾을 수 없습니다: {path}")
        with open(path, 'rb') as f:
            word_embeddings = pickle.load(f)
        return torch.tensor(word_embeddings, dtype=torch.float32).unsqueeze(0).to(self.device)
    
    def _create_transform(self):
        normalize = transforms.Normalize(mean=self.model.image_normalization_mean,
                                         std=self.model.image_normalization_std)
        return transforms.Compose([
            Warp(self.image_size),
            transforms.ToTensor(),
            normalize,
        ])

    def predict(self, img):
        # 이미지 전처리
        image = self.transform(img).unsqueeze(0).to(self.device)
        
        # 예측 수행
        with torch.no_grad():
            outputs = self.model(image, self.word_embeddings)
            probabilities = torch.softmax(outputs, dim=1)
            top3_prob, top3_catid = torch.topk(probabilities, 3)
        
        # 결과 처리
        top3_classes = top3_catid.squeeze().cpu().tolist()
        top3_probs = top3_prob.squeeze().cpu().tolist()
        
        return [(self.style_dict[cls], prob) for cls, prob in zip(top3_classes, top3_probs)]

# 사용 예시
if __name__ == '__main__':
    import time
    start_time = time.time()
    predictor = StylePredictor()  # 기본값 사용
    
    img_path = 'img/K-fashion(1)/105117/batch_GATE_225_09.jpg'
    img = Image.open(img_path)
    predictions = predictor.predict(img)

    
    for style, prob in predictions:
        print(f"{style}: {prob:.4f}")
    print(f"소요 시간: {time.time() - start_time:.2f}초")