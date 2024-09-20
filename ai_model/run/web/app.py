import sys
import os
import json

# 프로젝트 루트 디렉토리를 Python 경로에 추가
sys.path.append(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))

from run.predict.style_predictor_custom import StylePredictor
# from run.predict.caption.image_captioner import ImageCaptioner
# from run.predict.attribute_predictor_custom import AttributePredictor
from run.util.is_human import HumanDetector
from db.faiss_style_cos import StyleSearcher
from run.predict.image_captioner import ImageCaptioner
from PIL import Image
import numpy as np
import time
import io

from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)  # 모든 라우트에 대해 CORS를 허용합니다.

image_captioner = ImageCaptioner("thwri/CogFlorence-2.2-Large")
human_detector = HumanDetector()
style_predictor = StylePredictor()
style_searcher = StyleSearcher()
print("app.py 실행")

def get_image_from_request():
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
    
    # 이미지에서 사람 감지
    # results = human_detector.model(np.array(image))
    # if not human_detector._check_person(results):
    #     return None, ('이미지에 사람이 없습니다', 400)
    
    return image, None

@app.route('/style_predict', methods=['POST'])
def style_predict():
    image, error = get_image_from_request()
    if error:
        return error
    
    # 예측 수행
    predictions = style_predictor.predict(image)
    return jsonify({'predictions': predictions})

@app.route('/test', methods=['GET'])
def test():
    return jsonify({"message": "Flask is working!"})
# @app.route('/attribute_predict', methods=['POST'])
# def attribute_predict():
#     if 'image' not in request.files:
#         return jsonify({'error': '이미지가 없습니다'}), 400
    
#     file = request.files['image']
#     if file.filename == '':
#         return jsonify({'error': '선택된 파일이 없습니다'}), 400
    
#     if file:
#         # 이미지를 메모리에서 열기
#         image = Image.open(io.BytesIO(file.read()))
        
#         # 예측 수행
#         predictions = attribute_predictor.predict(image)
        
#         return jsonify({'predictions': predictions})
    
@app.route('/style_search', methods=['POST'])
def style_search():
    start_time = time.time()
    image, error = get_image_from_request()
    # print("스타일 검색 시작:")
    # print(f"이미지: {image}")
    if error:
        error_message, status_code = error
        print(f"에러: 상태 코드 {status_code}")
        print(f"에러 내용: {error_message}")
        return jsonify({"error": error_message}), status_code
    else:
        print("에러: 없음")
    print(f"요청 시간: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    if error:
        return error
    # 예측 수행
    style_result = style_predictor.predict(image)
    recom_result = style_searcher.get_similar_images_json(image)
    caption_result = json.loads(image_captioner.caption_and_translate(image))


    result = {
        'style_result': style_result,
        'recom_result': recom_result,
        'caption_result': caption_result['translated']
    }
    print(result)
    print(f"총 소요 시간: {time.time() - start_time} 초")
    return result

@app.route('/', methods=['GET'])
def print_predict():
    
    return 'print_predict'

if __name__ == '__main__':
    app.run(host='10.125.121.182', port=5000, debug=True)