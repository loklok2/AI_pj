import numpy as np
import pandas as pd
import faiss
from PIL import Image
import os
import sys
import json
os.environ['KMP_DUPLICATE_LIB_OK'] = 'True'
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from run.predict.style_predictor_custom import StylePredictor

class StyleSearcher:
    def __init__(self, csv_path='db/csv/image_style_vector.csv'):
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
        
        # 유클리드 거리 기반 인덱스 사용
        index = faiss.IndexFlatL2(dimension)
        index.add(features)

        return index, image_names, df

    def get_similar_images_json(self, image, k=5):
        similar_images = self.search_by_image(image, k)

        result = []
        for img, _, _ in similar_images:
            filename = os.path.basename(img)
            image_id = os.path.splitext(filename)[0]
            result.append(image_id)

        return result

    def search_similar_images(self, query_features, k=5):
        query_features = self.normalize_features(query_features.reshape(1, -1))

        # 유사도 검색을 수행합니다 (유클리드 거리 기준)
        distances, indices = self.index.search(query_features, k)

        # 결과의 개수를 출력하여 k 값과 일치하는지 확인
        print(f"검색된 결과 수: {len(indices[0])}, 요청된 k 값: {k}")

        # 유사도 순으로 정렬된 결과 반환
        results = [(self.image_names[idx], distances[0][i], self.df.iloc[idx]['style1']) 
                   for i, idx in enumerate(indices[0])]

        return results

    def search_by_image(self, image, k=5):
        feature = self.predictor.style_extractor(image)
        feature = self.normalize_features(feature.reshape(1, -1))

        return self.search_similar_images(feature, k)

if __name__ == '__main__':
    searcher = StyleSearcher()

    # 인덱스 저장 (선택사항)
    # faiss.write_index(searcher.index, "image_features_euclidean.index")

    # 예시: 이미지를 쿼리로 사용
    img_path = 'img/크롤링데이터/img/image_6.jpg'
    image = Image.open(img_path).convert('RGB')
    json_result = searcher.get_similar_images_json(image, k=15)
    print(json_result)
