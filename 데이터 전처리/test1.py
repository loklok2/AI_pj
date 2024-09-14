import csv
from collections import defaultdict

# 원본 CSV 파일 경로 (raw 문자열 사용)
input_file = r'C:\workspace_pj2\업체 자료\데이터 전처리\매장판매일보csv\매장판매일보(구서점).csv'

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

# 카테고리와 매장 ID를 위한 카운터
category_counter = 1
store_counter = 1
sale_counter = 1



# CSV 파일 읽기
with open(input_file, 'r', encoding='cp949') as csvfile:
    reader = csv.DictReader(csvfile)
    # 컬럼 이름 출력
    print("CSV 파일의 컬럼 이름:")
    print(reader.fieldnames)
    
    # 첫 번째 행 출력
    first_row = next(reader)
    print("\n첫 번째 행의 데이터:")
    for key, value in first_row.items():
        print(f"{key}: {value}")

print("\n프로그램 종료")



# CSV 파일 읽기
with open(input_file, 'r', encoding='cp949') as csvfile:
    reader = csv.DictReader(csvfile)
    for row in reader:
        # 매장 데이터 처리
        store_key = (row['매장구분'], row['매장코드'], row['매장명'])
        if store_key not in data['stores']:
            data['stores'][store_key] = store_counter
            store_counter += 1

        # 상품 데이터 처리
        product_key = row['상품코드']
        if product_key not in data['products']:
            data['products'][product_key] = {
                'name': row['상품명'],
                'team': row['팀구분'],
                'year': row['년'],
                'month': row['월'],
                'category': row['복종']
            }

        # 색상 데이터 처리
        color_key = row['칼라']
        if color_key not in data['colors']:
            data['colors'][color_key] = row['칼라명']

        # 사이즈 데이터 처리
        size_key = row['사이즈']
        if size_key not in data['sizes']:
            data['sizes'][size_key] = size_key

        # 카테고리 데이터 처리
        category_key = row['복종']
        if category_key not in data['categories']:
            data['categories'][category_key] = category_counter
            category_counter += 1

        # 판매 데이터 처리
        sale_data = {
            'date': row['판매일자'],
            'store_id': data['stores'][store_key],
            'product_id': product_key,
            'color_id': color_key,
            'size_id': size_key,
            'quantity': 1,  # 수량 정보가 없으므로 1로 가정
            'price_type': row['기획구분'],
            'sale_type': row['판매유형'],
            'transaction_type': row['판매구분']
        }
        data['sales'][sale_counter] = sale_data
        sale_counter += 1

# CSV 파일 쓰기
for key, filename in output_files.items():
    with open(filename, 'w', newline='', encoding='utf-8') as csvfile:
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
            fieldnames = ['product_id', 'product_name', 'team', 'year', 'month', 'category_id']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for product_id, product_info in data[key].items():
                writer.writerow({
                    'product_id': product_id,
                    'product_name': product_info['name'],
                    'team': product_info['team'],
                    'year': product_info['year'],
                    'month': product_info['month'],
                    'category_id': data['categories'][product_info['category']]
                })
        elif key == 'colors':
            fieldnames = ['color_id', 'color_name']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for color_id, color_name in data[key].items():
                writer.writerow({
                    'color_id': color_id,
                    'color_name': color_name
                })
        elif key == 'sizes':
            fieldnames = ['size_id', 'size_name']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for size_id, size_name in data[key].items():
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
            fieldnames = ['sale_id', 'sale_date', 'store_id', 'product_id', 'color_id', 'size_id', 'quantity', 'price_type', 'sale_type', 'transaction_type']
            writer = csv.DictWriter(csvfile, fieldnames=fieldnames)
            writer.writeheader()
            for sale_id, sale_info in data[key].items():
                writer.writerow({
                    'sale_id': sale_id,
                    'sale_date': sale_info['date'],
                    'store_id': sale_info['store_id'],
                    'product_id': sale_info['product_id'],
                    'color_id': sale_info['color_id'],
                    'size_id': sale_info['size_id'],
                    'quantity': sale_info['quantity'],
                    'price_type': sale_info['price_type'],
                    'sale_type': sale_info['sale_type'],
                    'transaction_type': sale_info['transaction_type']
                })

print("CSV 파일들이 성공적으로 생성되었습니다.")