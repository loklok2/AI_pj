import os
import csv
import time
from PIL import Image
import sys

# 프로젝트 루트 디렉토리를 파이썬 경로에 추가
project_root = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
sys.path.append(project_root)

from run.predict.attribute_predictor import AttributePredictor
from run.predict.style_predictor import StylePredictor
from run.util.is_human import HumanDetector

def process_images(root_folder, output_csv):
    output_dir = os.path.join(project_root, 'run', 'output')
    os.makedirs(output_dir, exist_ok=True)
    
    attribute_predictor = AttributePredictor()
    style_predictor = StylePredictor()
    human_detector = HumanDetector()

    output_path = os.path.join(output_dir, output_csv)
    with open(output_path, 'w', newline='', encoding='utf-8') as csvfile:
        csv_writer = csv.writer(csvfile)
        csv_writer.writerow(['의류 ID', '속성 유형', '속성 값'])
        
        for subdir, dirs, files in os.walk(root_folder):
            for file in files:
                if file.lower().endswith(('.png', '.jpg', '.jpeg', '.gif', '.bmp')):
                    img_path = os.path.join(subdir, file)
                    clothing_id = os.path.splitext(file)[0]
                    
                    try:
                        image = Image.open(img_path).convert('RGB')
                        image = human_detector.detect_and_execute(image)
                        if image:
                            human_detector.save_image(image)
                        attribute_results = attribute_predictor.predict(image)
                        style_results = style_predictor.predict(image)
                        
                        # 각 속성에 대한 결과를 저장할 리스트
                        rows_to_write = []
                        
                        # 스타일 처리
                        if style_results:
                            style = style_results[0][0]  # 가장 높은 확률의 스타일
                            rows_to_write.append([clothing_id, '스타일', style])
                        
                        # 카테고리 처리
                        if attribute_results.get('category') and attribute_results['category']:
                            category = attribute_results['category'][0][0]
                            rows_to_write.append([clothing_id, 'category', category])
                        
                        # 디테일 처리
                        if attribute_results.get('detail') and attribute_results['detail']:
                            detail = attribute_results['detail'][0][0]
                            rows_to_write.append([clothing_id, 'detail', detail])
                        
                        # texture 처리
                        if attribute_results.get('texture') and attribute_results['texture']:
                            texture = attribute_results['texture'][0][0]
                            rows_to_write.append([clothing_id, 'texture', texture])
                        
                        # print 처리
                        if attribute_results.get('print') and attribute_results['print']:
                            print_attr = attribute_results['print'][0][0]
                            rows_to_write.append([clothing_id, 'print', print_attr])
                        
                        # 결과 쓰기
                        csv_writer.writerows(rows_to_write)
                        
                    except Exception as e:
                        print(f"이미지 처리 중 오류 발생: {img_path}")
                        print(f"오류 내용: {str(e)}")

if __name__ == '__main__':
    start_time = time.time()
    print("start")
    root_folder = os.path.join('img', '여성가디건_images_20240904_105542')
    output_csv = os.path.join('clothing_attributes_wyolo.csv')
    
    process_images(root_folder, output_csv)
    
    print(f"처리 완료. 소요 시간: {time.time() - start_time:.2f}초")
