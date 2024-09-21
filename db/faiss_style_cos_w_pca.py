import numpy as np
import pandas as pd
import faiss
from PIL import Image
import os
import sys
import json
from sklearn.decomposition import PCA

os.environ['KMP_DUPLICATE_LIB_OK'] = 'True'
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from run.predict.style_predictor_custom import StylePredictor

class StyleSearcher:
    def __init__(self, csv_path='db/csv/image_style_vector.csv', pca_components=1024):
        self.predictor = StylePredictor()
        self.pca = PCA(n_components=pca_components)
        self.index, self.image_names, self.df = self.load_and_index_features(csv_path)

    @staticmethod
    def normalize_features(features):
        if features.ndim == 1:
            return features / np.linalg.norm(features)
        else:
            return features / np.linalg.norm(features, axis=1)[:, np.newaxis]

    def apply_pca(self, features):
        # PCA 설명력 확인
        reduced_features = self.pca.fit_transform(features)
        explained_variance = np.sum(self.pca.explained_variance_ratio_)
        print(f"PCA로 설명된 분산: {explained_variance * 100:.2f}%")
        
        # 축소된 피처 확인
        return self.normalize_features(reduced_features)

    def load_and_index_features(self, csv_path):
        df = pd.read_csv(csv_path)
        image_names = df['file_id'].tolist()

        feature_columns = [col for col in df.columns if col.startswith('feature_')]
        features = df[feature_columns].values.astype('float32')

        # PCA 적용
        features = self.apply_pca(features)

        dimension = features.shape[1]
        index = faiss.IndexFlatIP(dimension)
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

        similarities, indices = self.index.search(query_features, k)

        distances = 1 - similarities[0]
        print(f"검색된 결과 수: {len(indices[0])}, 요청된 k 값: {k}")

        results = [(self.image_names[idx], distances[i], self.df.iloc[idx]['style1'])
                   for i, idx in enumerate(indices[0])]

        return results

    def search_by_image(self, image, k=5):
        feature = self.predictor.style_extractor(image)
        feature = self.normalize_features(feature.reshape(1, -1))

        # PCA 적용 (fit이 아닌 transform으로 변경)
        feature = self.pca.transform(feature)

        return self.search_similar_images(feature, k)


if __name__ == '__main__':
    searcher = StyleSearcher(pca_components=256)

    img_path = 'img/크롤링데이터/img/image_6.jpg'
    image = Image.open(img_path).convert('RGB')
    json_result = searcher.get_similar_images_json(image, k=15)
    print(json_result)
