import pandas as pd
import os

# 폴더 경로 지정
폴더경로 = 'C:\workspace_pj2\업체 자료\데이터 전처리\매장판매일보csv'

# 대분류 매핑
대분류_매핑 = {
    'A': '의류', 'B': '잡화', 'C': '쥬얼리', 'D': '칸쥬', 'E': '화장품',
    'F': '생필품', 'K': '이벤트상품', 'N': '브랜드', 'X': '기타대',
    'Y': '상품권', 'Z': '커피', 'S': '오더', 'P': '식물'
}

# 년 분류 매핑
년_매핑 = {
    'Z': '공통', 'A': '2013', 'B': '2014', 'C': '2015', 'D': '2016',
    'E': '2017', 'F': '2018', 'G': '2019', 'H': '2020', 'I': '2021',
    'J': '2022', 'K': '2023', 'L': '2024'
}

# 월코드 매핑
월_매핑 = {
    'A': '1월', 'B': '2월', 'C': '3월', 'D': '4월', 'E': '5월', 'F': '6월',
    'G': '7월', 'H': '8월', 'I': '9월', 'J': '10월', 'K': '11월', 'L': '12월',
    'U': '공통', '0': '사계절', 'M': '샘플', '1': '1', '2': '2', 'Q': '퍼(FUR)', 'Z': '특종'
}

# 복종 매핑
복종_매핑 = {
    'TS': '티셔츠', 'TN': '티셔츠나시', 'KT': '니트', 'KN': '니트나시',
    'BL': '블라우스', 'WS': '남방', 'BN': '블라우스나시', 'JP': '점퍼',
    'JK': '자켓', 'CT': '코트', 'VT': '베스트', 'OP': '원피스',
    'CA': '가디건', 'PT': '바지', 'DP': '데님', 'SK': '스커트',
    'ST': '세트', 'LG': '레깅스', 'BG': '가방', 'MF': '머플러',
    'SB': '신발', 'CP': '모자', 'SC': '양말', 'GT': '기타',
    'AC': '스크래치특가', 'AK': '스크래치특가', 'BC': '부채',
    'BD': '바디제품', 'BT': '벨트', 'CD': '가디건1', 'CK': '시계2',
    'CL': '세척제', 'CO': '커피', 'ET': '기타대', 'FU': '퍼(FUR)',
    'GF': '상품권', 'GL': '안경', 'GV': '장갑', 'HR': '헤어제품',
    'IN': '이너웨어', 'JA': '키홀더', 'VI': '베스트', 'SL': '바지'
}

# 분류 함수
def 분류_추출(코드, 시작, 끝, 매핑):
    if pd.isna(코드):
        return '알 수 없음'
    코드 = str(코드)
    return 매핑.get(코드[시작:끝], '알 수 없음')

# 폴더 내의 모든 CSV 파일 처리
for 파일이름 in os.listdir(폴더경로):
    if 파일이름.endswith('.csv'):
        파일경로 = os.path.join(폴더경로, 파일이름)
        
        # CSV 파일 읽기
        df = pd.read_csv(파일경로, encoding='cp949')
        
        # 새 컬럼 추가
        df['대분류'] = df['상품코드'].apply(lambda x: 분류_추출(x, 0, 1, 대분류_매핑))
        df['년'] = df['상품코드'].apply(lambda x: 분류_추출(x, 1, 2, 년_매핑))
        df['월'] = df['상품코드'].apply(lambda x: 분류_추출(x, 2, 3, 월_매핑))
        df['주차'] = df['상품코드'].apply(lambda x: str(x)[3] if pd.notna(x) else '알 수 없음')
        df['복종'] = df['상품코드'].apply(lambda x: 분류_추출(x, 4, 6, 복종_매핑))
        df['넘버'] = df['상품코드'].apply(lambda x: str(x)[6:] if pd.notna(x) else '알 수 없음')
        
        # 파일 이름에서 괄호 안의 내용 추출
        괄호내용 = 파일이름.split('(')[1].split(')')[0] if '(' in 파일이름 and ')' in 파일이름 else ''
        
        # 결과 저장
        저장파일명 = f'분류된_상품데이터({괄호내용}).csv'
        저장경로 = os.path.join(폴더경로, 저장파일명)
        df.to_csv(저장경로, index=False, encoding='utf-8-sig')
        
        print(f'{파일이름} 처리 완료')

print('모든 파일 처리 완료')