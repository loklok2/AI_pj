from models.base_captioner import BaseCaptioner
import torch
from transformers import AutoModelForCausalLM, AutoProcessor
from PIL import Image
import requests
from google.cloud import translate_v2 as translate
from google.oauth2 import service_account
import json
import time
from io import BytesIO
import streamlit as st
from rembg import remove
import numpy as np
import re
import os
from config.settings import GOOGLE_CREDENTIALS_PATH

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

class ImageCaptioner(BaseCaptioner):
    def __init__(self, model_path):
        # Initialize model and processor
        self.model = AutoModelForCausalLM.from_pretrained(
            model_path, 
            trust_remote_code=True
        ).to(device)
        self.processor = AutoProcessor.from_pretrained(model_path, trust_remote_code=True)

        # Get credentials path from environment variable
        credentials_path = os.environ.get('GOOGLE_APPLICATION_CREDENTIALS')

        # If not set, get from settings.py
        if not credentials_path:
            credentials_path = GOOGLE_CREDENTIALS_PATH

        print(f"Attempting to load credentials from: {credentials_path}")

        if not os.path.exists(credentials_path):
            raise FileNotFoundError(f"Google credentials file not found at {credentials_path}")

        # Load Google credentials
        credentials = service_account.Credentials.from_service_account_file(
            credentials_path,
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
        elif hasattr(image_input, 'read'):  # File-like object
            img = Image.open(BytesIO(image_input.read()))
        else:
            raise ValueError(f"Unsupported image input type: {type(image_input)}")
        return img.convert('RGB')

    def remove_background(self, image):
        start_time = time.time()
        removed = remove(image)
        bg_removal_time = time.time() - start_time

        if isinstance(removed, Image.Image):
            return removed.convert('RGB'), bg_removal_time
        elif isinstance(removed, np.ndarray):
            return Image.fromarray(removed).convert('RGB'), bg_removal_time
        else:
            raise ValueError(f"Unexpected type returned by rembg: {type(removed)}")

    def generate_caption(self, image):
        start_time = time.time()
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
        caption_time = time.time() - start_time
        return caption, caption_time

    def post_process_caption(self, caption):
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
        
        clothing_description = re.search(r'(.*?(?:wearing|dressed in).*?\.)', caption)
        if clothing_description:
            caption = clothing_description.group(1)

        structured_description = {
            "Garment": "",
            "Color": "",
            "Style": "",
            "Details": ""
        }
        
        garment_types = ["cardigan", "trousers", "shirt", "dress", "skirt", "jacket", "coat", "sweater", "blouse", "pants"]
        for garment in garment_types:
            if garment in caption.lower():
                structured_description["Garment"] += f"{garment.capitalize()} "
        
        colors = ["blue", "white", "black", "red", "green", "yellow", "purple", "pink", "brown", "gray"]
        for color in colors:
            if color in caption.lower() and "background" not in caption.lower():
                structured_description["Color"] += f"{color.capitalize()} "
        
        style_elements = ["V-neck", "Round neck", "Buttoned", "Zip-up", "Loose-fit", "Tight-fit", "Sleeveless", "Long-sleeved", "Short-sleeved"]
        for element in style_elements:
            if element.lower() in caption.lower():
                structured_description["Style"] += f"{element} "
        
        structured_description["Details"] = caption.strip()

        return structured_description

    def translate_to_korean(self, text):
        start_time = time.time()
        result = self.translate_client.translate(text, target_language='ko')
        translation_time = time.time() - start_time
        return result['translatedText'], translation_time

    def caption_and_translate(self, image_input):
        start_time = time.time()
        
        image = self.load_image(image_input)
        image_no_bg, bg_removal_time = self.remove_background(image)
        
        caption, caption_time = self.generate_caption(image_no_bg)
        
        # Get the structured description as a dictionary
        structured_description = self.post_process_caption(caption)
        
        # Assemble the English output
        structured_output = ""
        counter = 1
        for key, value in structured_description.items():
            if value:
                structured_output += f"{counter}. {key}: {value.strip()}\n"
                counter += 1

        # Translate each field individually
        translated_description = {}
        for key, value in structured_description.items():
            translated_key, _ = self.translate_to_korean(key)
            translated_value, _ = self.translate_to_korean(value)
            translated_description[translated_key] = translated_value

        # Assemble the Korean output with proper line breaks
        structured_translated_output = ""
        counter = 1
        for key, value in translated_description.items():
            if value:
                structured_translated_output += f"{counter}. {key}: {value.strip()}\n"
                counter += 1
        
        total_time = time.time() - start_time

        print(f"Background removal time: {bg_removal_time:.4f} seconds")
        print(f"Caption generation time: {caption_time:.4f} seconds")
        print(f"Total processing time: {total_time:.4f} seconds")
        
        return json.dumps({
            'original': structured_output.strip(),
            'translated': structured_translated_output.strip(),
            'bg_removal_time': f"{bg_removal_time:.4f}",
            'caption_generation_time': f"{caption_time:.4f}",
            'total_processing_time': f"{total_time:.4f}"
        }, ensure_ascii=False)
