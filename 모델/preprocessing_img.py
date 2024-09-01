import json
from PIL import Image, ImageDraw
import os
import time
from tqdm import tqdm

def extract_data(one_labels):
    # print("one_labels", json.dumps(one_labels, ensure_ascii=False,indent=4, ))
    ID = one_labels["이미지 정보"]["이미지 식별자"]
    height = one_labels["이미지 정보"]["이미지 높이"]
    width = one_labels["이미지 정보"]["이미지 너비"]

    all_rects = one_labels["데이터셋 정보"]["데이터셋 상세설명"]["렉트좌표"]
    styles = one_labels["데이터셋 정보"]["데이터셋 상세설명"]["라벨링"]
    overall_style = list([one["스타일"] for one in styles["스타일"] if len(one.values()) > 0])
    if len(overall_style) == 0:
        overall_style = ["기타"]

    all_clothing_categories = list(all_rects.keys())
    
    rects = []
    clothing_categories = []
    for rect_idx, rect in enumerate(all_rects.values()):
        if len(rect[0].keys()) > 0:
            rects.append(rect)
            clothing_categories.append(all_clothing_categories[rect_idx])
    boxes = [] 
    # clothing_labels = []
    # style_labels = []
    
    
    all_attributes = {
        "material_categories": [], # "소재"
        "fit_categories": [], # "핏"
        "collar_categories": [],  # "칼라" 
        "neckline_categories": [], # "넥라인"
        "shirt_sleeve_categories": [], # "소매기장"
        "sleeve_categories": [], # "기장"
        "clothing_categories": [] # "카테고리"
    }
    for clothing_idx, cat in enumerate(clothing_categories):
        rect = rects[clothing_idx][0]
        # print("rect", rect)
        assert list(rect.keys()) == ["X좌표", "Y좌표", "가로", "세로"]
        box = list(rect.values())
        box[2] = box[0] + box[2]
        box[3] = box[1] + box[3]
        boxes.append(box) 

        # print("styles[cat]", styles[cat][])
        one_clothing_attributes = styles[cat][0]
        # clothing_labels.append(cat)
        for attribute_name, attribute_value in one_clothing_attributes.items():
            # print("attribute_name", attribute_name)
            if attribute_name == "카테고리":
                all_attributes["clothing_categories"].append(attribute_value)
            elif attribute_name == "기장":
                all_attributes["sleeve_categories"].append(attribute_value)
            elif attribute_name == "소매기장":
                all_attributes["shirt_sleeve_categories"].append(attribute_value)
            elif attribute_name == "핏":
                all_attributes["fit_categories"].append(attribute_value)
            elif attribute_name == "소재":
                all_attributes["material_categories"].append(attribute_value)
            elif attribute_name == "옷깃":
                all_attributes["collar_categories"].append(attribute_value)
    
        for key, value in all_attributes.items():
            if len(value) <= clothing_idx:
                # print("append")
                if key == "카테고리":
                    all_attributes[key].append(clothing_categories[clothing_idx])
                else:
                    all_attributes[key].append(0)
    obj = {
        "ID": ID,
        "overall_style": overall_style,
        "height": height,
        "width": width,
        "boxes": boxes,
        "labels": clothing_categories,
        "attributes": all_attributes
    }
    return obj


def saveimg_bbox(image, name, boxes):
    img = Image.open(image)
    img1 = ImageDraw.Draw(img)  
    for box in boxes:
        img1.rectangle(box, outline="red")
    img.save(name)
    
def find_image_file(image_id, image_dir):
    """
    주어진 image_id에 해당하는 이미지 파일을 image_dir에서 찾습니다.
    """
    # image_id를 문자열로 변환
    image_id_str = str(image_id)
    
    for root, dirs, files in os.walk(image_dir):
        for file in files:
            # 파일명이 image_id로 시작하는지 확인 (문자열 비교)
            if file.startswith(image_id_str) and file.endswith('.jpg'):
                return os.path.join(root, file)
    return None

def replace_root_directory(root_path, old_root, new_root):
    """
    주어진 경로(root_path)의 상위 디렉토리를 old_root에서 new_root로 바꿉니다.
    """
    relative_path = os.path.relpath(root_path, old_root)
    return os.path.join(new_root, relative_path)

if __name__ == "__main__":
    # 현재 작업 디렉토리
    base_dir = os.getcwd()
    sub_dir = "리조트"
    # 원본 파일들의 디렉토리
    LABELING_DIR = os.path.join(base_dir, "data/Validation/라벨링데이터")
    IMAGE_DIR = os.path.join(base_dir, "data/Validation/원천데이터")

    # 저장할 파일들의 새로운 디렉토리
    NEW_LABELING_DIR = os.path.join(base_dir, "data/Validation/라벨링데이터_전처리")
    NEW_IMAGE_DIR = os.path.join(base_dir, "data/Validation/원천데이터_전처리")
    
    success_count = 0
    error_count = 0
    
    # 작업 시작 시간 기록
    start_time = time.time()
    print("============Start Preprocessing============")
    
    # os.walk에 tqdm 적용하여 디렉토리별 진행상황 표시
    for root, dirs, files in tqdm(os.walk(LABELING_DIR), desc="Processing directories"):
        # JSON 파일 필터링
        json_files = [file for file in files if file.endswith('.json')]
        
        # 각 파일 처리에 대해 tqdm 진행바 추가
        for file in tqdm(json_files, desc="Processing files", leave=False):
            json_file_path = os.path.join(root, file)
            full_data = json.load(open(json_file_path, "r", encoding='utf-8'))
            extracted = extract_data(full_data)

            img_file = find_image_file(extracted["ID"], IMAGE_DIR)
            if img_file:
                new_img_root = replace_root_directory(img_file, IMAGE_DIR, NEW_IMAGE_DIR)
                new_img_dir = os.path.dirname(new_img_root)
                if not os.path.exists(new_img_dir):
                    os.makedirs(new_img_dir)
                saveimg_bbox(img_file, os.path.join(new_img_dir, f'{extracted["ID"]}_box.jpg'), extracted["boxes"])

                new_json_path = replace_root_directory(json_file_path, LABELING_DIR, NEW_LABELING_DIR)
                new_json_dir = os.path.dirname(new_json_path)
                if not os.path.exists(new_json_dir):
                    os.makedirs(new_json_dir)
                json.dump(
                    extracted, 
                    open(new_json_path, "w", encoding='utf8'), 
                    ensure_ascii=False,
                    indent=4, 
                )
                success_count += 1
            else:
                print(f"Image file for {extracted['ID']} not found.")
                error_count += 1
    
    # 작업 종료 시간 기록
    end_time = time.time()
    elapsed_time = end_time - start_time

    # 최종 결과 출력
    print(f"\nProcessing completed. Success: {success_count} files, Errors: {error_count} files.")
    print(f"Time elapsed: {elapsed_time:.2f} seconds")