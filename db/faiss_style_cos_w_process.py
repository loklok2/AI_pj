import os
import numpy as np
import pandas as pd
import faiss
from PIL import Image
import cv2
import torch
from transformers import CLIPProcessor, CLIPModel
os.environ['KMP_DUPLICATE_LIB_OK']='True'
import sys
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from run.predict.style_predictor_custom import StylePredictor

class StyleSearcher:
    def __init__(self, csv_path='db/csv/image_style_vector.csv', image_dir='img/이미지인덱싱'):
        self.predictor = StylePredictor()
        self.index, self.image_names, self.df = self.load_and_index_features(csv_path)
        self.image_dir = image_dir
        # SIFT 초기화
        self.sift = cv2.SIFT_create()
        # CLIP 모델 및 프로세서 초기화
        self.clip_model = CLIPModel.from_pretrained("openai/clip-vit-base-patch32")
        self.processor = CLIPProcessor.from_pretrained("openai/clip-vit-base-patch32")

    @staticmethod
    def normalize_features(features):
        """특징 벡터를 정규화합니다."""
        if features.ndim == 1:
            return features / np.linalg.norm(features)
        else:
            return features / np.linalg.norm(features, axis=1)[:, np.newaxis]

    def load_and_index_features(self, csv_path):
        """이미지 스타일 벡터를 로드하고 FAISS 인덱스를 생성합니다."""
        df = pd.read_csv(csv_path)
        image_names = df['file_id'].tolist()

        feature_columns = [col for col in df.columns if col.startswith('feature_')]
        features = df[feature_columns].values.astype('float32')

        features = self.normalize_features(features)

        dimension = features.shape[1]
        index = faiss.IndexFlatIP(dimension)  # 코사인 유사도를 위한 인덱스
        index.add(features)

        return index, image_names, df

    def resize_image(self, image, size=(224, 224)):
        """이미지 크기를 고정된 크기로 리사이즈합니다."""
        return image.resize(size, Image.LANCZOS)  # ANTIALIAS 대신 LANCZOS 사용

    # SIFT 특징점 추출 및 비교
    def extract_sift_features(self, image):
        """이미지의 SIFT 특징점 추출."""
        image = np.array(image)
        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
        keypoints, descriptors = self.sift.detectAndCompute(gray, None)
        return keypoints, descriptors

    def compare_sift_features(self, des1, des2):
        """두 이미지의 SIFT 특징점을 비교."""
        bf = cv2.BFMatcher(cv2.NORM_L2, crossCheck=True)
        matches = bf.match(des1, des2)
        return len(matches)

    # 엣지 감지 및 비교
    def extract_edges(self, image):
        """Canny 엣지 검출로 이미지의 경계선 추출."""
        image = np.array(image)
        gray = cv2.cvtColor(image, cv2.COLOR_RGB2GRAY)
        edges = cv2.Canny(gray, 100, 200)
        return edges

    def compare_edges(self, edges1, edges2):
        """엣지 맵 비교 - 두 엣지 맵의 차이를 계산."""
        # 입력 이미지의 크기가 일치하는지 확인하고 차이가 나면 리사이즈 필요
        if edges1.shape != edges2.shape:
            print(f"엣지 크기 불일치: {edges1.shape}, {edges2.shape}")
            return float('inf')  # 크기가 다르면 차이를 무한대로 처리

        difference = cv2.absdiff(edges1, edges2)
        return np.sum(difference)  # 차이의 총합 반환

    # CLIP 임베딩 추출 및 비교
    def extract_clip_embedding(self, image):
        """CLIP 모델을 사용해 이미지의 임베딩을 추출."""
        inputs = self.processor(images=image, return_tensors="pt")
        with torch.no_grad():
            embedding = self.clip_model.get_image_features(**inputs)
        return embedding

    def compare_embeddings(self, emb1, emb2):
        """두 임베딩 간 유사도를 계산 (코사인 유사도)."""
        emb1 = emb1 / emb1.norm(dim=-1, keepdim=True)
        emb2 = emb2 / emb2.norm(dim=-1, keepdim=True)
        return torch.matmul(emb1, emb2.T).item()

    def search_similar_images(self, query_features, k=5):
        """특정 이미지 특징 벡터로 상위 k개의 유사 이미지를 검색합니다."""
        query_features = self.normalize_features(query_features.reshape(1, -1))

        # 전체 데이터셋에 대해 유사도 검색 수행
        similarities, indices = self.index.search(query_features, k)
        distances = 1 - similarities[0]

        # 검색된 결과 반환 (이미지 파일명, 거리, 스타일1)
        results = [(self.image_names[idx], distances[i], self.df.iloc[idx]['style1'])
                   for i, idx in enumerate(indices[0])]
        return results

    def search_by_image(self, image, k=5, filter_by_pattern=False, filter_by_edges=False, filter_by_clip=False):
        """이미지를 입력으로 받아 여러 후처리 방법으로 필터링된 유사 이미지를 반환."""
        # 이미지의 특징 벡터 추출 및 유사도 검색
        feature = self.predictor.style_extractor(image)
        feature = self.normalize_features(feature.reshape(1, -1))
        similar_images = self.search_similar_images(feature, k)

        # 패턴 기반 후처리 (SIFT)
        if filter_by_pattern:
            query_keypoints, query_descriptors = self.extract_sift_features(image)
            filtered_images = []
            for img_name, distance, style in similar_images:
                img_path = os.path.join(self.image_dir, img_name)
                candidate_image = Image.open(img_path).convert('RGB')
                _, candidate_descriptors = self.extract_sift_features(candidate_image)

                # SIFT 특징점 비교
                match_count = self.compare_sift_features(query_descriptors, candidate_descriptors)
                if match_count > 30:  # 일정 매칭 기준
                    filtered_images.append((img_name, distance, style, match_count))

            filtered_images.sort(key=lambda x: x[3], reverse=True)
            return filtered_images[:k]

        # 엣지 기반 후처리 (Canny)
        if filter_by_edges:
            # 쿼리 이미지 엣지 추출
            resized_query_image = self.resize_image(image)  # 크기를 통일
            query_edges = self.extract_edges(resized_query_image)
            filtered_images = []

            for img_name, distance, style in similar_images:
                img_path = os.path.join(self.image_dir, img_name)
                candidate_image = Image.open(img_path).convert('RGB')

                # 후보 이미지 리사이즈 후 엣지 추출
                resized_candidate_image = self.resize_image(candidate_image)
                candidate_edges = self.extract_edges(resized_candidate_image)

                # 엣지 맵 비교
                edge_diff = self.compare_edges(query_edges, candidate_edges)
                if edge_diff < 20000:  # 엣지 차이 기준
                    filtered_images.append((img_name, distance, style, edge_diff))

            filtered_images.sort(key=lambda x: x[3])  # 엣지 차이를 기준으로 정렬
            return filtered_images[:k]

        # CLIP 임베딩 기반 후처리
        if filter_by_clip:
            query_embedding = self.extract_clip_embedding(image)
            filtered_images = []
            for img_name, distance, style in similar_images:
                img_path = os.path.join(self.image_dir, img_name)
                candidate_image = Image.open(img_path).convert('RGB')
                candidate_embedding = self.extract_clip_embedding(candidate_image)

                # CLIP 임베딩 비교
                clip_similarity = self.compare_embeddings(query_embedding, candidate_embedding)
                if clip_similarity > 0.5:  # CLIP 유사도 기준
                    filtered_images.append((img_name, distance, style, clip_similarity))

            filtered_images.sort(key=lambda x: x[3], reverse=True)
            return filtered_images[:k]

        # 기본 유사도 검색 결과 반환
        return similar_images

if __name__ == '__main__':
    searcher = StyleSearcher()

    # 예시: 이미지를 쿼리로 사용
    img_path = 'img/크롤링데이터/img/image_1.jpg'
    image = Image.open(img_path).convert('RGB')

    # 패턴 기반 후처리
    json_result_pattern = searcher.search_by_image(image, k=15, filter_by_pattern=True)
    print("SIFT-based pattern filtering:", json_result_pattern)

    # 엣지 기반 후처리
    json_result_edges = searcher.search_by_image(image, k=15, filter_by_edges=True)
    print("Edge-based filtering:", json_result_edges)

    # CLIP 기반 후처리
    json_result_clip = searcher.search_by_image(image, k=15, filter_by_clip=True)
    print("CLIP-based filtering:", json_result_clip)
