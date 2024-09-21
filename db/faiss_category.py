import numpy as np
import pandas as pd
import faiss
import os
import sys
from PIL import Image
os.environ['KMP_DUPLICATE_LIB_OK']='True'
# 상대 경로를 사용하여 StylePredictor 임포트
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from run.predict.attribute_predictor_custom import AttributePredictor

class CategorySearcher:
    def __init__(self, csv_path='output/image_category_features.csv', model_name='category'):
        self.predictor = AttributePredictor()
        self.csv_path = csv_path
        self.model_name = model_name
        self.index, self.image_names, self.df = self.load_and_index_features()

    def load_and_index_features(self):
        df = pd.read_csv(self.csv_path)
        image_names = df['file_id'].tolist()
        
        feature_columns = [col for col in df.columns if col.startswith('feature_')]
        features = df[feature_columns].values.astype('float32')
        
        dimension = features.shape[1]
        index = faiss.IndexFlatL2(dimension)
        index.add(features)
        
        return index, image_names, df

    def search_similar_images(self, image, k=5):
        
        query_features = self.predictor.extract_features(image)[self.model_name]
        
        distances, indices = self.index.search(query_features.reshape(1, -1), k)
        
        similar_images = [(self.image_names[idx], distances[0][i]) for i, idx in enumerate(indices[0])]
        return similar_images

    def save_index(self, filename):
        faiss.write_index(self.index, filename)

    def search_and_print_results(self, img_path, k=5):
        similar_images = self.search_similar_images(img_path, k)

        print(f"유사한 이미지 ({self.model_name} 특성 기반 유클리드 거리):")
        for img, distance in similar_images:
            print(f"이미지: {img}, 거리: {distance}")
        
        category_columns = ['category1', 'category2', 'category3']
        if all(col in self.df.columns for col in category_columns):
            query_categories = self.df[category_columns].iloc[0].tolist()
            print(f"\n쿼리 이미지 카테고리: {', '.join(query_categories)}")
            
            for img, _ in similar_images:
                similar_categories = self.df[self.df['file_id'] == img][category_columns].values[0].tolist()
                print(f"유사 이미지 {img} 카테고리: {', '.join(similar_categories)}")

# 사용 옛
if __name__ == '__main__':
    searcher = CategorySearcher()  # 기본값 사용

    searcher.save_index("category_features_euclidean.index")

    img_path = 'C:/SS/AI_project_data/kfashoin_ai_model/img/크롤링데이터/여성가디건/1.jpg'
    image = Image.open(img_path).convert('RGB')
    searcher.search_and_print_results(image)