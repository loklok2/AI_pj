import numpy as np
import pandas as pd
import faiss
from PIL import Image
import os
import sys
import json
os.environ['KMP_DUPLICATE_LIB_OK']='True'
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from run.predict.style_predictor_custom import StylePredictor

class MultiFeatureStyleSearcher:
    def __init__(self, csv_paths=None):
        base_dir = os.path.dirname(os.path.abspath(__file__))
        # 기본 CSV 경로 설정
        if csv_paths is None:
            csv_paths = {
                'category': os.path.join(base_dir, 'csv/category_features.csv'),
                'texture': os.path.join(base_dir, 'csv/texture_features.csv'),
                'print': os.path.join(base_dir, 'csv/print_features.csv'),
                'details': os.path.join(base_dir, 'csv/detail_features.csv')
            }
        
        self.predictor = StylePredictor()
        self.index_dict = {}
        self.image_names_dict = {}
        self.df_dict = {}
        for model_name, csv_path in csv_paths.items():
            self.index_dict[model_name], self.image_names_dict[model_name], self.df_dict[model_name] = self.load_and_index_features(csv_path)

    @staticmethod
    def normalize_features(features):
        if features.ndim == 1:
            return features / np.linalg.norm(features)
        else:
            return features / np.linalg.norm(features, axis=1)[:, np.newaxis]

    def load_and_index_features(self, csv_path):
        df = pd.read_csv(csv_path)
        image_names = df['file_id'].tolist()
        
        feature_columns = [col for col in df.columns if col.startswith('feature_')]
        features = df[feature_columns].values.astype('float32')
        
        features = self.normalize_features(features)
        
        dimension = features.shape[1]
        index = faiss.IndexFlatIP(dimension)  # 코사인 유사도를 위한 IP (Inner Product) 사용
        index.add(features)
        
        return index, image_names, df

    def search_similar_images(self, query_features, model_name, k=5):
        index = self.index_dict[model_name]
        query_features = self.normalize_features(query_features.reshape(1, -1))
        
        # 해당 카테고리에 대해 유사도 검색 수행
        similarities, indices = index.search(query_features, k)
        
        distances = 1 - similarities[0]  # 코사인 유사도를 거리로 변환
        
        results = [(self.image_names_dict[model_name][idx], distances[i], model_name) 
                for i, idx in enumerate(indices[0])]
        
        return results

    def search_by_image(self, image, model_name, k=5):
        feature = self.predictor.style_extractor(image)
        feature = self.normalize_features(feature.reshape(1, -1))

        # 해당 feature 모델에 대해 유사도 검색
        return self.search_similar_images(feature, model_name, k)

    def get_similar_images_json(self, image, k=5):
        # 모든 model_name에 대해 검색
        result = {}
        feature = self.predictor.style_extractor(image)
        feature = self.normalize_features(feature.reshape(1, -1))

        for model_name in self.index_dict.keys():
            similar_images = self.search_similar_images(feature, model_name, k)

            model_result = []
            for img, _, model in similar_images:
                filename = os.path.basename(img)
                image_id = os.path.splitext(filename)[0]
                model_result.append(image_id)
            
            result[model_name] = model_result

        return result


if __name__ == '__main__':
    searcher = MultiFeatureStyleSearcher()

    # 예시: 이미지를 쿼리로 사용하여 모든 feature 기반으로 검색
    img_path = 'img/크롤링데이터/img/image_6.jpg'
    image = Image.open(img_path).convert('RGB')
    json_result = searcher.get_similar_images_json(image, k=5)
    print(json_result)
