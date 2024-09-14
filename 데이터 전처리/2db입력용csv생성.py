import csv
from collections import defaultdict
import datetime
import re
import os

# 입력 CSV 파일들이 있는 폴더 경로
input_folder = r'C:\workspace_pj2\업체 자료\데이터 전처리\분류전처리적용csv'

# 출력할 CSV 파일들 (절대 경로 사용)
output_files = {
    'stores': r'C:\workspace_pj2\업체 자료\데이터 전처리\stores.csv',
    'products': r'C:\workspace_pj2\업체 자료\데이터 전처리\products.csv',
    'colors': r'C:\workspace_pj2\업체 자료\데이터 전처리\colors.csv',
    'sizes': r'C:\workspace_pj2\업체 자료\데이터 전처리\sizes.csv',
    'categories': r'C:\workspace_pj2\업체 자료\데이터 전처리\categories.csv',
    'sales': r'C:\workspace_pj2\업체 자료\데이터 전처리\sales.csv'
}

# 각 테이블의 데이터를 저장할 딕셔너리
data = defaultdict(dict)

# 카운터 초기화
counters = {
    'category': 1,
    'store': 1,
    'product': 1,
    'color': 1,
    'size': 1,
    'sale': 1
}

# 인코딩 목록
encodings = ['utf-8', 'euc-kr', 'cp949']

# '월' 문자열에서 숫자만 추출하는 함수
def extract_month_number(month_string):
    number = re.search(r'\d+', month_string)
    if number:
        return int(number.group())
    return None

# CSV 파일 읽기 함수
def read_csv_file(file_path):
    for encoding in encodings:
        try:
            with open(file_path, 'r', encoding=encoding) as csvfile:
                reader = csv.DictReader(csvfile)
                for row in reader:
                    process_row(row)
            print(f"파일을 성공적으로 읽었습니다: {file_path}")
            return
        except UnicodeDecodeError:
            print(f"{encoding} 인코딩으로 읽기 실패. 다음 인코딩 시도 중...")
        except Exception as e:
            print(f"오류 발생: {e}")
            return
    print(f"모든 인코딩 시도 실패. 파일을 읽을 수 없습니다: {file_path}")

# 행 처리 함수
def process_row(row):
    # 매장 데이터 처리
    store_key = (row['매장구분'], row['매장코드'], row['매장명'])
    if store_key not in data['stores']:
        data['stores'][store_key] = counters['store']
        counters['store'] += 1
    store_id = data['stores'][store_key]

    # 제품 데이터 처리
    product_key = (row['상품코드'], row['상품명'])
    if product_key not in data['products']:
        data['products'][product_key] = counters['product']
        counters['product'] += 1
    product_id = data['products'][product_key]

    # 색상 데이터 처리
    color_key = (row['칼라'], row['칼라명'])
    if color_key not in data['colors']:
        data['colors'][color_key] = counters['color']
        counters['color'] += 1
    color_id = data['colors'][color_key]

    # 사이즈 데이터 처리
    size_key = row['사이즈']
    if size_key not in data['sizes']:
        data['sizes'][size_key] = counters['size']
        counters['size'] += 1
    size_id = data['sizes'][size_key]

    # 카테고리 데이터 처리
    category_key = row['복종']
    if category_key not in data['categories']:
        data['categories'][category_key] = counters['category']
        counters['category'] += 1
    category_id = data['categories'][category_key]

    # 판매 데이터 처리
    sale_date = datetime.datetime.strptime(row['판매일자'], '%Y-%m-%d').date()
    sale_data = {
        'sale_id': counters['sale'],
        'sale_date': sale_date,
        'store_id': store_id,
        'product_id': product_id,
        'color_id': color_id,
        'size_id': size_id,
        'quantity': 1,  # 수량을 항상 1로 설정
        'price_type': row['기획구분'],
        'sale_type': row['판매유형'],
        'transaction_type': row['판매구분'],
        'year': int(row['년']),
        'month': extract_month_number(row['월'])
    }
    data['sales'][counters['sale']] = sale_data
    counters['sale'] += 1

# 모든 CSV 파일 처리
for filename in os.listdir(input_folder):
    if filename.endswith('.csv'):
        file_path = os.path.join(input_folder, filename)
        read_csv_file(file_path)

# CSV 파일 쓰기
for key, output_file in output_files.items():
    with open(output_file, 'w', newline='', encoding='cp949') as csvfile:
        if key == 'stores':
            fieldnames = ['store_id', 'store_type', 'store_code', 'store_name']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for (store_type, store_code, store_name), store_id in data[key].items():
                writer.writerow({
                    'store_id': store_id,
                    'store_type': store_type,
                    'store_code': store_code,
                    'store_name': store_name
                })
        elif key == 'products':
            fieldnames = ['product_id', 'product_code', 'product_name']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for (product_code, product_name), product_id in data[key].items():
                writer.writerow({
                    'product_id': product_id,
                    'product_code': product_code,
                    'product_name': product_name
                })
        elif key == 'colors':
            fieldnames = ['color_id', 'color_code', 'color_name']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for (color_code, color_name), color_id in data[key].items():
                writer.writerow({
                    'color_id': color_id,
                    'color_code': color_code,
                    'color_name': color_name
                })
        elif key == 'sizes':
            fieldnames = ['size_id', 'size_name']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for size_name, size_id in data[key].items():
                writer.writerow({
                    'size_id': size_id,
                    'size_name': size_name
                })
        elif key == 'categories':
            fieldnames = ['category_id', 'category_name']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for category_name, category_id in data[key].items():
                writer.writerow({
                    'category_id': category_id,
                    'category_name': category_name
                })
        elif key == 'sales':
            fieldnames = ['sale_id', 'sale_date', 'store_id', 'product_id', 'color_id', 'size_id', 'quantity', 'price_type', 'sale_type', 'transaction_type', 'year', 'month']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for sale_id, sale_info in data[key].items():
                writer.writerow(sale_info)

print("CSV 파일들이 성공적으로 생성되었습니다.")