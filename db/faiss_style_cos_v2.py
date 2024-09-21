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
        index = faiss.IndexFlatIP(dimension)
        index.add(features)
        
        return index, image_names, df

    def get_similar_images_json(self, image, k=5):
        # search_by_image 메서드 호출 시 k 값을 전달합니다.
        similar_images = self.search_by_image(image, k)

        result = []
        for img, _ in similar_images:
            # 이미지 경로에서 파일명만 추출
            filename = os.path.basename(img)
            # 확장자 제거
            image_id = os.path.splitext(filename)[0]
            result.append(image_id)

        return result
    def search_similar_images(self, query_features, k=5, styles=None):
        query_features = self.normalize_features(query_features.reshape(1, -1))
        
        if styles is not None:
            # 스타일이 포함된 데이터만 필터링 (styles 리스트의 모든 값이 style1, style2, style3 중에 존재해야 함)
            filtered_indices = self.df[self.df[['style1', 'style2', 'style3']].apply(
                lambda row: set(styles).issubset(set(row.values)), axis=1)].index.to_numpy()  # 3개 스타일이 모두 있어야 함
            
            if len(filtered_indices) == 0:
                print("해당 스타일을 가진 이미지가 없습니다.")
                return []

            # 해당 인덱스에 해당하는 features만 추출
            features_filtered = np.array([self.index.reconstruct(int(idx)) for idx in filtered_indices])
            
            # 새로운 FAISS 인덱스 생성
            index_filtered = faiss.IndexFlatIP(query_features.shape[1])
            index_filtered.add(features_filtered)
            
            # 필터링된 인덱스로 검색
            similarities, faiss_indices = index_filtered.search(query_features, k)
            
            # 필터링된 인덱스를 원래 데이터 인덱스에 매핑
            filtered_results = [(self.image_names[filtered_indices[idx]], 1 - similarities[0][i]) 
                                for i, idx in enumerate(faiss_indices[0])]
        else:
            # 스타일 필터링이 없는 경우 전체 데이터셋에 대해 검색
            similarities, indices = self.index.search(query_features, k)
            filtered_results = [(self.image_names[idx], 1 - similarities[0][i]) 
                                for i, idx in enumerate(indices[0])]

        # 결과의 개수를 출력하여 k 값과 일치하는지 확인
        print(f"검색된 결과 수: {len(filtered_results)}, 요청된 k 값: {k}")
        
        return filtered_results


    def search_by_image(self, image, k=5):
        feature = self.predictor.style_extractor(image)
        feature = self.normalize_features(feature.reshape(1, -1))
        style = self.predictor.predict(image)
        # search_similar_images 메서드 호출 시 k 값을 전달합니다.
        return self.search_similar_images(feature, k, style)


if __name__ == '__main__':
    searcher = StyleSearcher()

    # 인덱스 저장 (선택사항)
    # faiss.write_index(searcher.index, "image_features_cosine.index")

    # 예시: 이미지를 쿼리로 사용
    img_path = 'img/크롤링데이터/img/image_6.jpg'
    image = Image.open(img_path).convert('RGB')
    json_result = searcher.get_similar_images_json(image, k=15)
    print(json_result)
