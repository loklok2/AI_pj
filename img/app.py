import streamlit as st
from image_captioner import ImageCaptioner
from PIL import Image
import json

# Initialize the ImageCaptioner
@st.cache_resource
def load_model():
    return ImageCaptioner("thwri/CogFlorence-2.2-Large")

captioner = load_model()

st.title("Image Captioning and Translation")

uploaded_file = st.file_uploader("Choose an image...", type=["jpg", "jpeg", "png"])

if uploaded_file is not None:
    image = Image.open(uploaded_file).convert('RGB')
    st.image(image, caption='Uploaded Image', use_column_width=True)
    
    if st.button('Generate Caption'):
        with st.spinner('Generating caption...'):
            result = captioner.caption_and_translate(image)
            result_dict = json.loads(result)
            
            st.subheader("Original Caption:")
            st.write(result_dict['original'])
            
            st.subheader("Translated Caption (Korean):")
            st.write(result_dict['translated'])

            # 처리 시간 표시
            st.write(f"배경 제거 시간: {result_dict.get('bg_removal_time', '데이터 없음')} 초")
            st.write(f"캡션 생성 시간: {result_dict.get('caption_generation_time', '데이터 없음')} 초")
            st.write(f"번역 시간: {result_dict.get('translation_time', '데이터 없음')} 초")
            st.write(f"총 처리 시간: {result_dict.get('total_processing_time', '데이터 없음')} 초")
