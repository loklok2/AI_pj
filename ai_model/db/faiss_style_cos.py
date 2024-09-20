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

class StyleSearcher:
    def __init__(self, csv_path='output/image_features(2).csv'):
        self.predictor = StylePredictor()
        self.index, self.image_names, self.df = self.load_and_index_features(csv_path)

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
        print(f"CSV 특징 벡터 차원: {dimension}")
        index = faiss.IndexFlatIP(dimension)
        index.add(features)
        
        return index, image_names, df
    def get_similar_images_json(self, image, k=5):
        similar_images = self.search_by_image(image, k)
        
        result = []
        for img, _, _ in similar_images:
            # 이미지 경로에서 파일명만 추출
            filename = os.path.basename(img)
            # 확장자 제거
            image_id = os.path.splitext(filename)[0]
            result.append(image_id)
        
        return result

    def search_similar_images(self, query_features, k=5):
        query_features = self.normalize_features(query_features.reshape(1, -1))
        
        # 전체 데이터셋에 대해 유사도 검색 수행
        similarities, indices = self.index.search(query_features, k)
        
        distances = 1 - similarities[0]
        
        # 유사도 순으로 정렬된 결과 반환
        results = [(self.image_names[idx], distances[i], self.df.iloc[idx]['style1']) 
                   for i, idx in enumerate(indices[0])]
        
        return results

    def search_by_image(self, image, k=5):
        feature = self.predictor.style_extractor(image)
        
        feature = self.normalize_features(feature.reshape(1, -1))
        
        return self.search_similar_images(feature)

    def print_similar_images(self, similar_images):
        print("유사한 이미지 (코사인 유사도 기반, 같은 스타일):")
        for img, distance, style in similar_images:
            similarity = 1 - distance
            print(f"이미지: {img}, 유사도: {similarity:.4f}, 스타일: {style}")

    def print_style_info(self, similar_images):
        style_columns = ['style1', 'style2', 'style3']
        if all(col in self.df.columns for col in style_columns):
            query_styles = self.df[style_columns].iloc[0].tolist()
            # 정수를 문자열로 변환
            query_styles = [str(style) for style in query_styles]
            print(f"\n쿼리 이미지 스타일: {', '.join(query_styles)}")
            
            for img, _, _ in similar_images:  # 세 개의 값을 언패킹
                similar_styles = self.df[self.df['file_id'] == img][style_columns].values[0].tolist()
                # 정수를 문자열로 변환
                similar_styles = [str(style) for style in similar_styles]
                print(f"유사 이미지 {img} 스타일: {', '.join(similar_styles)}")


if __name__ == '__main__':
    searcher = StyleSearcher()

    # 인덱스 저장 (선택사항)
    # faiss.write_index(searcher.index, "image_features_cosine.index")

    # 예시: 이미지를 쿼리로 사용
    img_path = 'img/이미지인덱싱/14.jpg'
    image = Image.open(img_path).convert('RGB')
    json_result = searcher.get_similar_images_json(image)
    print(json_result)

    # # 기존 출력 방식 (선택적)
    similar_images = searcher.search_by_image(image)
    searcher.print_similar_images(similar_images)
    searcher.print_style_info(similar_images)