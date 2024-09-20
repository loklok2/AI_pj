from models.base_captioner import BaseCaptioner
import open_clip
import torch
from PIL import Image
import json
import io

class FashionCLIP(BaseCaptioner):
    def __init__(self):
        # (기존 초기화 코드)

     def generate_caption(self, image_input):
        if isinstance(image_input, str):
            image = Image.open(image_input).convert('RGB')
        elif isinstance(image_input, Image.Image):
            image = image_input.convert('RGB')
        else:  # 파일 업로드 객체 처리
            image = Image.open(io.BytesIO(image_input.read())).convert('RGB')
        
        image = self.preprocess_val(image).unsqueeze(0).to(self.device)

        text_queries = ["a hat", "a t-shirt", "shoes"]  # 필요에 따라 확장
        text = self.tokenizer(text_queries).to(self.device)

        with torch.no_grad(), torch.cuda.amp.autocast():
            image_features = self.model.encode_image(image)
            text_features = self.model.encode_text(text)
            image_features /= image_features.norm(dim=-1, keepdim=True)
            text_features /= text_features.norm(dim=-1, keepdim=True)

            text_probs = (100.0 * image_features @ text_features.T).softmax(dim=-1)

        return text_queries[torch.argmax(text_probs).item()]

    def caption_and_translate(self, image_input):
        caption = self.generate_caption(image_input)
        # 번역 기능 구현 필요
        return json.dumps({
            'original': caption,
            'translated': caption,  # 실제로는 번역된 텍스트로 교체해야 함
            'processing_times': {
                'caption_generation': 0.0,  # 실제 시간 측정 필요
                'translation': 0.0  # 실제 시간 측정 필요
            }
        })