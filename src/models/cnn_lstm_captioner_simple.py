from models.base_captioner import BaseCaptioner
import torch
import torch.nn as nn
import torch.nn.functional as F
import torchvision.transforms as transforms
from PIL import Image
import json
import io
import os

from efficientnet_pytorch import EfficientNet  # EfficientNet 사용
from transformers import DistilBertTokenizer, DistilBertModel  # DistilBERT 사용
import time

class SimpleCNNLSTMCaptioner(BaseCaptioner):
    def __init__(self, image_folder, caption_file, embed_size=256):
        self.image_folder = image_folder
        self.caption_file = caption_file
        self.transform = transforms.Compose([
            transforms.Resize((128, 128)),
            transforms.ToTensor(),
            transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225])
        ])
        self.vocab = self.build_vocab()
        self.encoder = EncoderCNN(embed_size)
        self.decoder = DecoderTransformer(embed_size, len(self.vocab))
        self.tokenizer = DistilBertTokenizer.from_pretrained('distilbert-base-uncased')

    def build_vocab(self):
        # 간단한 어휘 사전 구현 (필요시 수정)
        return {'<pad>': 0, '<start>': 1, '<end': 2, 'shirt': 3, 'pants': 4, 'red': 5, 'blue': 6}

    def generate_caption(self, image):
        if isinstance(image, str):
            image = Image.open(image).convert('RGB')
        elif isinstance(image, Image.Image):
            image = image.convert('RGB')
        else:  # 파일 업로드 객체 처리
            image = Image.open(io.BytesIO(image.read())).convert('RGB')
        
        image = self.transform(image).unsqueeze(0)
        
        start_time = time.time()
        
        with torch.no_grad():
            self.encoder.eval()
            self.decoder.eval()
            feature = self.encoder(image)
            print("Encoder output shape:", feature.shape)  # 디버깅을 위한 출력
            sampled_ids = self.decoder.sample(feature)
        
        # 모델 추론 시간 계산
        caption_time = time.time() - start_time
        
        sampled_caption = []
        for word_id in sampled_ids[0]:
            word = list(self.vocab.keys())[list(self.vocab.values()).index(word_id.item())]
            sampled_caption.append(word)
            if word == '<end>':
                break
        return ' '.join(sampled_caption[1:-1]), caption_time  # <start>와 <end> 제거

    def caption_and_translate(self, image):
        caption, caption_time = self.generate_caption(image)
        
        start_translation = time.time()
        # 번역 로직 추가 가능
        translated_caption = caption  # TODO: 실제 번역 코드 추가
        translation_time = time.time() - start_translation

        return json.dumps({
            'original': caption,
            'translated': translated_caption,
            'processing_times': {
                'caption_generation': round(caption_time, 4),
                'translation': round(translation_time, 4)
            }
        })

class EncoderCNN(nn.Module):
    def __init__(self, embed_size):
        super(EncoderCNN, self).__init__()
        self.efficientnet = EfficientNet.from_pretrained('efficientnet-b0')
        self.fc = nn.Linear(1280, embed_size)  # EfficientNet-B0의 출력 크기는 1280입니다
        self.bn = nn.BatchNorm1d(embed_size, momentum=0.01)

    def forward(self, images):
        features = self.efficientnet.extract_features(images)
        features = features.mean([2, 3])  # Global Average Pooling
        features = self.fc(features)
        features = self.bn(features)
        return features



class DecoderTransformer(nn.Module):
    def __init__(self, embed_size, vocab_size):
        super(DecoderTransformer, self).__init__()
        self.embed_size = embed_size
        self.vocab_size = vocab_size
        self.word_embed = nn.Embedding(vocab_size, embed_size)
        self.embed = nn.Linear(embed_size, 768)  # 입력 특징을 DistilBERT 임베딩 크기로 변환
        self.transformer = DistilBertModel.from_pretrained('distilbert-base-uncased')
        self.fc = nn.Linear(self.transformer.config.hidden_size, vocab_size)

    def forward(self, features):
        embedded = self.embed(features).unsqueeze(1)  # (batch_size, 1, 768)
        transformer_output = self.transformer(inputs_embeds=embedded).last_hidden_state
        outputs = self.fc(transformer_output)
        return outputs

    def sample(self, features, max_len=20):
        print("Features shape:", features.shape)  # 디버깅을 위한 출력
        sampled_ids = []
        inputs = self.embed(features).unsqueeze(1)  # (batch_size, 1, 768)
        
        for _ in range(max_len):
            outputs = self.transformer(inputs_embeds=inputs).last_hidden_state
            outputs = self.fc(outputs.squeeze(1))  # Remove sequence dimension
            _, predicted = outputs.max(1)
            sampled_ids.append(predicted.unsqueeze(1))
            
            # 다음 입력을 위해 예측된 단어를 임베딩
            predicted_embed = self.word_embed(predicted)
            inputs = self.embed(predicted_embed).unsqueeze(1)
        
        sampled_ids = torch.cat(sampled_ids, 1)  # (batch_size, max_len)
        return sampled_ids