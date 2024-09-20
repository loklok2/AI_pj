import pandas as pd
import os

# CSV 파일 경로
csv_file = 'output/image_features(2)_removed.csv'

# 이미지가 있는 폴더 경로
image_folder = 'img\이미지인덱싱'

# CSV 파일 읽기 (첫 번째 행 무시, 두 번째 행을 헤더로 사용)
df = pd.read_csv(csv_file, encoding='cp949', header=1)

# 삭제할 파일 목록 생성
files_to_remove = [os.path.basename(path) for path in df['removed_image_path']]

# 사용자 확인
print(f"총 {len(files_to_remove)}개의 파일이 삭제될 예정입니다.")
confirm = input("정말로 이 파일들을 삭제하시겠습니까? (y/n): ")

if confirm.lower() != 'y':
    print("작업이 취소되었습니다.")
    exit()

# 파일 삭제
removed_count = 0
for file in files_to_remove:
    file_path = os.path.join(image_folder, file)
    if os.path.exists(file_path):
        try:
            os.remove(file_path)
            print(f"삭제됨: {file_path}")
            removed_count += 1
        except Exception as e:
            print(f"삭제 실패: {file_path} - 오류: {e}")
    else:
        print(f"파일이 존재하지 않음: {file_path}")

print(f"작업 완료. {removed_count}개의 파일이 삭제되었습니다.")