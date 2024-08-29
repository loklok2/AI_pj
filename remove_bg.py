import os
from PIL import Image
from rembg import remove

def remove_backgrounds_in_folder(folder_path):
    """"""
    # 상위 폴더 경로
    parent_folder = os.path.dirname(folder_path)

    # 결과를 저장할 폴더명 생성
    output_folder_name = f"without_bg_{os.path.basename(folder_path)}"
    output_folder_path = os.path.join(parent_folder, output_folder_name)

    # 결과 폴더가 없으면 생성
    os.makedirs(output_folder_path, exist_ok=True)

    # 폴더 내 모든 파일 처리
    for filename in os.listdir(folder_path):
        if filename.endswith((".png", ".jpg", ".jpeg")):  # 이미지 파일 확장자 확인
            # 파일 경로
            file_path = os.path.join(folder_path, filename)
            
            # 이미지 열기
            input_image = Image.open(file_path)
            
            # 배경 제거
            output_image = remove(input_image)
            # RGBA 모드인 경우 RGB로 변환
            if output_image.mode == 'RGBA':
                output_image = output_image.convert('RGB')
            # 결과 파일 저장
            output_image_path = os.path.join(output_folder_path, filename)
            output_image.save(output_image_path)
            print(f"Saved without background: {output_image_path}")

    print(f"All images processed. Results saved in {output_folder_path}.")

if __name__ == "__main__":
    # 사용자에게 폴더 경로를 입력받기
    folder_path = input("Enter the path to the folder containing images: ")

    # 배경 제거 작업 수행
    remove_backgrounds_in_folder(folder_path)
