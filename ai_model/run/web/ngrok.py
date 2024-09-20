# 실행 방법 : streamlit run ngrok.py

import streamlit as st
import matplotlib.pyplot as plt
from PIL import Image
from style_predictor_no_bg import StylePredictor
from is_human import HumanDetector
import koreanize_matplotlib 
# st.set_option("deprecation.showfileUploaderEncoding", False)
style_predictor = StylePredictor()
human_detector = HumanDetector()
# 한글 폰트 설정

st.sidebar.title("이미지 인식 앱")
st.sidebar.write("원본 이미지 인식 모델을 사용해서 무슨 이미지인지를 판정합니다.")

st.sidebar.write("")

img_source = st.sidebar.radio("이미지 소스를 선택해 주세요", ("이미지를 업로드", "카메라로 촬영"))
if img_source == "이미지를 업로드" :
  img_file = st.sidebar.file_uploader("이미지를 선택해 주세요.", type= ['png', 'jpg', 'jpeg'])
elif img_source =='카메라로 촬영':
  img_file = st.camera_input("카메라로 촬영")

if img_file is not None :
  with st.spinner("측정중...") :
    try:
      img = Image.open(img_file)
      st.image(img, caption= "대상 이미지", width=480)
      st.write("")

      # 예측
      # img = human_detector.detect_and_execute(img)
      results = style_predictor.predict(img)
      # 결과 표시
      st.subheader("판정 결과")
      n_top = 3
      for label, prob in results:
          st.write(f"{round(prob*100, 2)}%의 확률로 {label}입니다.")

      pie_labels = [label for label, _ in results]
      pie_probs = [prob for _, prob in results]

      fig, ax = plt.subplots()
      wedgeprops = {"width": 0.3, "edgecolor": "white"}
      textprops = {"fontsize": 6}
      ax.pie(pie_probs,
            labels=pie_labels,
            counterclock=False,
            startangle=90,
            textprops=textprops,
            autopct="%.2f",
            wedgeprops=wedgeprops)
      # 범례 설정 (옵션)
      
      st.pyplot(fig)
    except Exception as e:
      st.error(f"이미지 처리 중 오류가 발생했습니다: {e}")

  st.sidebar.write("")
  st.sidebar.write("")

  st.sidebar.caption("""
  이 앱은 K-Fashion를 훈련 데이터로 사용하고 있습니다.""")