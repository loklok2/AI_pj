import sys
import os
import json
import requests

# 프로젝트 루트 디렉토리를 Python 경로에 추가
sys.path.append(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))
from run.web.admin_page import *
from run.predict.style_predictor_custom import StylePredictor
from run.predict.attribute_predictor_custom import AttributePredictor
from db.faiss_style_cos_v2 import StyleSearcher
from PIL import Image
import numpy as np
import pandas as pd
import time
import io

from flask import Flask, request, jsonify
from flask_cors import CORS

# 플라스크 설정
app = Flask(__name__, static_folder='public')
CORS(app)  # 모든 라우트에 대해 CORS를 허용합니다.

# 모델 불러오기
attribute_predictor = AttributePredictor()
style_predictor = StylePredictor()
style_searcher = StyleSearcher()
processor = SegformerImageProcessor.from_pretrained("mattmdjaga/segformer_b2_clothes")
model = AutoModelForSemanticSegmentation.from_pretrained("mattmdjaga/segformer_b2_clothes")
print("서버 구동")

def get_image_from_product(product):
    """BE 서버에서 이미지를 불러오는 메서드"""
    # URL 생성
    url = f"http://10.125.121.188:8080{product['pimgPath']}"
    
    # 이미지 가져오기
    response = requests.get(url)
    if response.status_code == 200:
        image = Image.open(io.BytesIO(response.content))
        return image
    else:
        raise Exception("이미지를 가져오는 데 실패했습니다.")
    
def get_image_from_request():
    """request에서 이미지를 불러오는 메서드"""
    if request.data:
        try:
            image = Image.open(io.BytesIO(request.data))
        except IOError:
            return None, ('유효하지 않은 이미지 데이터입니다', 400)
    elif 'file' in request.files:
        file = request.files['file']
        if file.filename == '':
            return None, ('선택된 파일이 없습니다', 400)
        image = Image.open(file)
    else:
        return None, ('이미지 데이터가 없습니다', 400)    
    return image, None

@app.route('/attribute_predict', methods=['POST'])
def attribute_predict():
    start_time = time.time()
    image, error = get_image_from_request()
    if error:
        error_message, status_code = error
        print(f"에러: 상태 코드 {status_code}")
        print(f"에러 내용: {error_message}")
        return jsonify({"error": error_message}), status_code
    else:
        print("에러: 없음")

    predictions = attribute_predictor.predict(image)
    
    # 결과를 모델 이름별로 정리
    formatted_predictions = {}
    for model_name, model_predictions in predictions.items():
        formatted_predictions[model_name] = [
            {"label": label}
            for label in model_predictions  # model_predictions는 이제 레이블 리스트입니다
        ]
    
    print(f"총 소요 시간: {time.time() - start_time} 초")
    return jsonify({'predictions': formatted_predictions})

@app.route('/admin_search', methods=['POST'])
def admin_search():
    if request.data:
        try:
            data = json.loads(request.data)
        except json.JSONDecodeError:
            return jsonify({"error": "유효하지 않은 JSON 데이터입니다."}), 400
        
        result_list = []
        for item in data:
            crawl_folder = f"run/web/public/images/{item['productId']}"
            output_folder = os.path.join(crawl_folder, "processed_images")
            category = [item['category']]
            img = get_image_from_product(item)
            df = crawl_image(category, crawl_folder, 500)
            process_and_save_images(crawl_folder, output_folder, category)
            extract_category_features_to_csv(output_folder, output_folder)
            csv_paths = {
                'category': os.path.join(output_folder, 'category_features.csv'),
                'texture': os.path.join(output_folder, 'texture_features.csv'),
                'print': os.path.join(output_folder, 'print_features.csv'),
                'details': os.path.join(output_folder, 'detail_features.csv')
            }
            attribute_searcher = MultiFeatureStyleSearcher(csv_paths)
            _, processed_images = process_image(img, processor, model, category)[0]
            json_result = attribute_searcher.get_similar_images_json(processed_images)
    else:
        return jsonify({"error": "데이터가 없습니다."}), 400
    # JSON 반환을 위한 딕셔너리 초기화
    result = {}

    # 각 카테고리에 대해 처리
    for category, thumbnails in json_result.items():
        result[category] = {}
        # 각 썸네일에 해당하는 상품 정보 추출
        for thumbnail in thumbnails:
            key = thumbnail.split('_')[-1]  # "thumbnail_2"에서 숫자만 추출
            product_info = df[df['pimgPath'].str.contains(f'thumbnail_{key}')]
            
            if not product_info.empty:
                # 해당 썸네일의 상품 정보를 딕셔너리 형태로 추가
                result[category][thumbnail] = product_info.to_dict(orient='records')[0]

    # 최종 결과를 JSON으로 변환하여 반환 (Content-Type: application/json)
    return jsonify(result)

@app.route('/style_search', methods=['POST'])
def style_search():
    start_time = time.time()
    image, error = get_image_from_request()
    if error:
        error_message, status_code = error
        print(f"에러: 상태 코드 {status_code}")
        print(f"에러 내용: {error_message}")
        return jsonify({"error": error_message}), status_code
    else:
        print("에러: 없음")
    if error:
        return error
    # 예측 수행
    style_result = style_predictor.predict(image)
    recom_result = style_searcher.get_similar_images_json(image)
    # caption_result = image_captioner.caption_and_translate(image)

    result = {
        'style_result': style_result,
        'recom_result': recom_result,
        # 'caption_result': caption_result
    }
    print(result)
    print(f"총 소요 시간: {time.time() - start_time} 초")
    return result




if __name__ == '__main__':
    app.run(host='10.125.121.187', port=5000
            , debug=True
            , use_reloader=False
            )