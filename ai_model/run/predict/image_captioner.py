import torch
from transformers import AutoModelForCausalLM, AutoProcessor
from PIL import Image
import requests
from google.cloud import translate_v2 as translate
from google.oauth2 import service_account
import json
import time
from io import BytesIO
from rembg import remove
import numpy as np
import re

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

class ImageCaptioner:
    def __init__(self, model_path):
        self.model = AutoModelForCausalLM.from_pretrained(
            model_path, 
            trust_remote_code=True
        ).to(device)
        self.processor = AutoProcessor.from_pretrained(model_path, trust_remote_code=True)

        credentials = service_account.Credentials.from_service_account_file(
            'C:/Users/user/Desktop/AIProject/00. Project/data2/google_translate/google_credentials.json',
            scopes=["https://www.googleapis.com/auth/cloud-platform"]
        )
        self.translate_client = translate.Client(credentials=credentials)

    def load_image(self, image_input):
        if isinstance(image_input, str):
            if image_input.startswith('http'):
                response = requests.get(image_input)
                img = Image.open(BytesIO(response.content))
            else:
                img = Image.open(image_input)
        elif isinstance(image_input, Image.Image):
            img = image_input
        else:
            raise ValueError("Unsupported image input type")
        return img.convert('RGB')

    def remove_background(self, image):
        start_time = time.time()  # 배경 제거 시작 시간
        removed = remove(image)
        bg_removal_time = time.time() - start_time  # 배경 제거 소요 시간

        if isinstance(removed, Image.Image):
            return removed.convert('RGB'), bg_removal_time
        elif isinstance(removed, np.ndarray):
            return Image.fromarray(removed).convert('RGB'), bg_removal_time
        else:
            raise ValueError(f"Unexpected type returned by rembg: {type(removed)}")

    def generate_caption(self, image):
        start_time = time.time()  # 캡션 생성 시작 시간
        prompt = "<MORE_DETAILED_CAPTION>"
        inputs = self.processor(text=prompt, images=image, return_tensors="pt").to(device)
        with torch.no_grad():
            generated_ids = self.model.generate(
                input_ids=inputs["input_ids"],
                pixel_values=inputs["pixel_values"],
                max_new_tokens=100,
                num_beams=5,
                do_sample=True,
                temperature=0.7,
                no_repeat_ngram_size=2,
                length_penalty=1.0
            )
        caption = self.processor.batch_decode(generated_ids, skip_special_tokens=True)[0].replace(prompt, "").strip()
        caption_time = time.time() - start_time  # 캡션 생성 소요 시간
        return self.post_process_caption(caption), caption_time

    def post_process_caption(self, caption):
        # 불필요한 정보(포즈, 악세사리, 배경 등) 필터링
        irrelevant_phrases = [
            "The image shows", "In this image", "The photograph depicts",
            "We can see", "This picture features", "The model is wearing",
            "The person in the image", "Against a", "The background",
            "Seated on", "Their pose", "The subject", "portrait", "close-up",
            "wicker chair", "black background", "resting", "raised", "revealing",
            "manicure", "nail polish", "posing", "accessories", "hand", "legs", "posture"
        ]
        for phrase in irrelevant_phrases:
            caption = caption.replace(phrase, "")
        
        # 옷에 대한 설명만 추출 (wearing, dressed in 등의 패턴으로 필터링)
        clothing_description = re.search(r'(.*?(?:wearing|dressed in).*?\.)', caption)
        if clothing_description:
            caption = clothing_description.group(1)

        # 문장 정리 및 구조화
        structured_description = {
            "Garment": "",
            "Color": "",
            "Style": "",
            "Details": ""
        }
        
        # 의복 종류 추출
        garment_types = ["cardigan", "trousers", "shirt", "dress", "skirt", "jacket", "coat", "sweater", "blouse", "pants"]
        for garment in garment_types:
            if garment in caption.lower():
                structured_description["Garment"] += f"{garment.capitalize()} "
        
        # 색상 추출
        colors = ["blue", "white", "black", "red", "green", "yellow", "purple", "pink", "brown", "gray"]
        for color in colors:
            if color in caption.lower() and "background" not in caption.lower():
                structured_description["Color"] += f"{color.capitalize()} "
        
        # 스타일 및 디자인 요소 추출
        style_elements = ["V-neck", "round neck", "buttoned", "zip-up", "loose-fit", "tight-fit", "sleeveless", "long-sleeved", "short-sleeved"]
        for element in style_elements:
            if element.lower() in caption.lower():
                structured_description["Style"] += f"{element} "
        
        # 나머지 세부 사항
        structured_description["Details"] = caption

        # 결과를 하나의 문자열로 결합하여 반환
        result = ""
        for key, value in structured_description.items():
            if value:
                result += f"{key}: {value.strip()}\n"
        
        return result.strip()


    def translate_to_korean(self, text):
        start_time = time.time()  # 번역 시작 시간
        result = self.translate_client.translate(text, target_language='ko')
        translation_time = time.time() - start_time  # 번역 소요 시간
        return result['translatedText'], translation_time

    def caption_and_translate(self, image_input):
        start_time = time.time()  # 전체 처리 시간 시작
        
        # 배경 제거
        image = self.load_image(image_input)
        image_no_bg, bg_removal_time = self.remove_background(image)
        
        # 캡션 생성
        caption, caption_time = self.generate_caption(image_no_bg)
        
        # 번역
        translated_caption, translation_time = self.translate_to_korean(caption)
        
        total_time = time.time() - start_time  # 전체 처리 시간 종료
        
        # CMD 창에 시간 출력
        # print(f"Background removal time: {bg_removal_time:.4f} seconds")
        # print(f"Caption generation time: {caption_time:.4f} seconds")
        # print(f"Translation time: {translation_time:.4f} seconds")
        # print(f"Total processing time: {total_time:.4f} seconds")
        
        # 처리 시간 반환
        return json.dumps({
            'original': caption,
            'translated': translated_caption,
            'bg_removal_time': f"{bg_removal_time:.4f}",
            'caption_generation_time': f"{caption_time:.4f}",
            'translation_time': f"{translation_time:.4f}",
            'total_processing_time': f"{total_time:.4f}"
        }, ensure_ascii=False)

if __name__ == "__main__":
    captioner = ImageCaptioner("thwri/CogFlorence-2.2-Large")
    
    url = "https://huggingface.co/datasets/huggingface/documentation-images/resolve/main/transformers/tasks/car.jpg?download=true"
    url_result = captioner.caption_and_translate(url)
    # print("URL result:", url_result)

    path_result = captioner.caption_and_translate("C:/Users/user/Desktop/AIProject/Data Analysis/AIModel/kfashoin_ai_model/img/K-fashion(2)/199444/Dduddu_463_06.jpg")
    # print("File path result:", path_result)

    image = Image.open("C:/Users/user/Desktop/AIProject/Data Analysis/AIModel/kfashoin_ai_model/img/K-fashion(2)/199444/Dduddu_463_06.jpg")
    image_result = captioner.caption_and_translate(image)
    # print("Image object result:", image_result)
