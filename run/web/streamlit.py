import streamlit as st
import matplotlib.pyplot as plt
from PIL import Image
import requests
import os
import io
from dotenv import load_dotenv
import csv
from datetime import datetime
import shutil

# .env 파일 로드
load_dotenv()

# 결과 및 이미지 저장 함수
def save_result_and_image(img_file, style_results, recommendation_results, satisfaction, user_style=None):
    # 이미지 저장
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    img_name = f"input_{timestamp}.jpg"
    img_dir = "web/img"  # 수정된 이미지 저장 경로
    if not os.path.exists(img_dir):
        os.makedirs(img_dir)
    img_path = os.path.join(img_dir, img_name)
    
    with Image.open(img_file) as img:
        img.save(img_path)

    # CSV에 결과 저장
    csv_file = 'web/style_results.csv'  # 수정된 CSV 저장 경로
    file_exists = os.path.isfile(csv_file)
    
    with open(csv_file, 'a', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        if not file_exists:
            writer.writerow(['Timestamp', 'Image', 'Style Results', 'Recommendations', 'Satisfaction', 'User Suggested Style'])
        
        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        writer.writerow([timestamp, img_path, ', '.join(style_results), ', '.join(recommendation_results), satisfaction, user_style])

    return img_path

# streamlit 코드
def main():
    st.sidebar.title("이미지 인식 앱")
    st.sidebar.write("원본 이미지 인식 모델을 사용해서 무슨 이미지인지를 판정합니다.")

    st.sidebar.write("")

    # 세션 상태 초기화
    if 'submitted' not in st.session_state:
        st.session_state.submitted = False
    if 'current_image' not in st.session_state:
        st.session_state.current_image = None
    if 'show_style_selection' not in st.session_state:
        st.session_state.show_style_selection = False

    img_source = st.sidebar.radio("이미지 소스를 선택해 주세요", ("이미지를 업로드", "카메라로 촬영"))
    if img_source == "이미지를 업로드":
        img_file = st.sidebar.file_uploader("이미지를 선택해 주세요.", type=['png', 'jpg', 'jpeg'])
    elif img_source == '카메라로 촬영':
        img_file = st.camera_input("카메라로 촬영")
    
    if img_file is not None:
        # 새 이미지가 업로드되면 submitted 상태 초기화
        if st.session_state.current_image != img_file:
            st.session_state.submitted = False
            st.session_state.current_image = img_file
            st.session_state.show_style_selection = False

        with st.spinner("측정중..."):
            try:
                img = Image.open(img_file)
                st.image(img, caption="대상 이미지", width=480)
                st.write("")
                
                # 이미지를 바이트로 변환
                img_byte_arr = io.BytesIO()
                img.save(img_byte_arr, format='PNG')
                img_byte_arr = img_byte_arr.getvalue()

                # 서버에 요청 보내기
                response = requests.post('http://10.125.121.187:5000/style_search', files={'file': img_byte_arr})
                if response.status_code == 200:
                    results = response.json()
                else:
                    st.error(f"서버 요청 실패: {response.status_code}")
                    st.stop()

                st.subheader("추천 결과")

                # recom_result 이미지 표시
                recom_images = results['recom_result']
                cols = st.columns(5)  # 5개의 열 생성

                for idx, img_name in enumerate(recom_images):
                    img_path = os.path.join('img', '이미지인덱싱', f'{img_name}.jpg')
                    if os.path.exists(img_path):
                        img = Image.open(img_path)
                        cols[idx].image(img, caption=f"추천 {idx+1}", use_column_width=True)
                    else:
                        cols[idx].write(f"이미지를 찾을 수 없음: {img_name}")

                # style_result 표시
                st.subheader("스타일 분류 결과")
                style_labels = {0: "클래식", 1: "매니시", 2: "페미니", 3: "에스닉", 4: "컨템포러리",
                                5: "내추럴", 6: "젠더리스", 7: "스포티", 8: "서브컬처", 9: "캐주얼"}
                style_results = [style_labels[style_id] for style_id in results['style_result']]
                for style in style_results:
                    st.write(f"- {style}")

                # 만족도 조사 라디오 버튼 추가
                if not st.session_state.submitted:
                    st.write("")
                    st.write("스타일 분류 결과에 만족하셨나요?")
                    satisfaction = st.radio("만족도를 선택해주세요", ("만족", "보통", "불만족"), index=None)
                    
                    # 제출 버튼 추가
                    if st.button("제출"):
                        if satisfaction:
                            if satisfaction == "불만족":
                                st.session_state.show_style_selection = True
                            else:
                                saved_img_path = save_result_and_image(img_file, style_results, recom_images, satisfaction)
                                st.success(f"'{satisfaction}' 의견이 제출되었습니다. 감사합니다!")
                                st.session_state.submitted = True
                        else:
                            st.warning("만족도를 선택해주세요.")
                    
                    # 불만족 시 스타일 선택
                    if st.session_state.show_style_selection:
                        st.write("이 이미지에 적합한 스타일을 선택해주세요 (최대 2개):")
                        user_style = st.multiselect("스타일 선택", list(style_labels.values()), max_selections=2)
                        if st.button("스타일 제출"):
                            if user_style:
                                if len(user_style) <= 2:
                                    saved_img_path = save_result_and_image(img_file, style_results, recom_images, satisfaction, ', '.join(user_style))
                                    st.success("의견이 제출되었습니다. 감사합니다!")
                                    st.session_state.submitted = True
                                    st.session_state.show_style_selection = False
                                else:
                                    st.warning("최대 2개의 스타일만 선택할 수 있습니다.")
                            else:
                                st.warning("적어도 하나의 스타일을 선택해주세요.")
                else:
                    st.info("이 이미지에 대한 만족도가 이미 제출되었습니다.")

            except Exception as e:
                st.error(f"이미지 처리 중 오류가 발생했습니다: {e}")

    st.sidebar.write("")
    st.sidebar.write("")
    st.sidebar.caption("""
    이 앱은 K-Fashion를 훈련 데이터로 사용하고 있습니다.""")

if __name__ == "__main__":
    main()