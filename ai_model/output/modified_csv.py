import pandas as pd
import os

# CSV 파일 경로
input_csv = 'output/image_features(2)_removed.csv'
output_csv = 'output/modified_image_features.csv'

# CSV 파일 읽기 (첫 번째 행 무시, 두 번째 행을 헤더로 사용)
df = pd.read_csv(input_csv, encoding='cp949', header=1)

# 파일 이름만 추출 (확장자 제외)
def extract_filename_without_extension(path):
    return os.path.splitext(os.path.basename(path))[0]

# removed_image_path에서 확장자를 제외한 파일 이름만 추출하여 새로운 열 생성
df['removed_image_path'] = df['removed_image_path'].apply(extract_filename_without_extension)

# original_image_path에서도 확장자를 제외한 파일 이름만 추출
df['original_image_path'] = df['original_image_path'].apply(extract_filename_without_extension)

# 수정된 DataFrame을 새 CSV 파일로 저장
df.to_csv(output_csv, index=False, encoding='cp949')

print(f"수정된 CSV 파일이 {output_csv}에 저장되었습니다.")

# 처리된 행의 수와 첫 몇 개의 행 출력
print(f"총 {len(df)} 개의 행이 처리되었습니다.")
print("\n처리된 데이터의 첫 5개 행:")
print(df.head())