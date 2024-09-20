import streamlit as st
from models.blip2_flan_t5_xl import ImageCaptioner
from models.cnn_lstm_captioner import CNNLSTMCaptioner
from models.cnn_lstm_captioner_simple import SimpleCNNLSTMCaptioner
from models.fashion_clip import FashionCLIP
from config.settings import (
    CAPTION_MODEL_PATH, IMAGE_FOLDER, CAPTION_FILE,
    CNN_LSTM_EMBED_SIZE, CNN_LSTM_HIDDEN_SIZE, CNN_LSTM_NUM_LAYERS
)
import json

st.set_page_config(page_title="Image Captioning App", layout="wide")

@st.cache_resource
def load_captioner(model_type):
    captioners = {
        "blip2_flan_t5_xl": lambda: ImageCaptioner(CAPTION_MODEL_PATH),
        "cnn_lstm": lambda: CNNLSTMCaptioner(
            IMAGE_FOLDER, CAPTION_FILE,
            embed_size=CNN_LSTM_EMBED_SIZE,
            hidden_size=CNN_LSTM_HIDDEN_SIZE,
            num_layers=CNN_LSTM_NUM_LAYERS
        ),
        "simple_cnn_lstm": lambda: SimpleCNNLSTMCaptioner(
            image_folder="path/to/image/folder",
            caption_file="path/to/caption/file",
            embed_size=256  # hidden_size 제거
        ),
        "fashion_clip": lambda: FashionCLIP()
    }
    return captioners[model_type]()

def main():
    st.title("Image Captioning App")

    model_types = ["blip2_flan_t5_xl", "cnn_lstm", "simple_cnn_lstm", "fashion_clip"]
    model_type = st.selectbox("Select Model", model_types)
    
    captioner = load_captioner(model_type)

    uploaded_file = st.file_uploader("Choose an image...", type=["jpg", "png", "jpeg"])

    if uploaded_file is not None:
        st.image(uploaded_file, caption='Uploaded Image.', use_column_width=True)
        st.write("")
        st.write("Generating caption...")

        result = captioner.caption_and_translate(uploaded_file)
        result_dict = json.loads(result)

        st.write("Original Caption:")
        st.write(result_dict['original'])

        if 'translated' in result_dict:
            st.write("Korean Translation:")
            st.write(result_dict['translated'])

        if 'processing_times' in result_dict:
            st.write("Processing Times:")
            for key, value in result_dict['processing_times'].items():
                st.write(f"{key}: {value} seconds")

if __name__ == "__main__":
    main()