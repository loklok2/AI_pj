from models.base_captioner import BaseCaptioner
import torch
import torch.nn as nn
import torchvision.models as models
import torchvision.transforms as transforms
from torch.utils.data import Dataset, DataLoader
from PIL import Image
import os
import json
import io

class CNNLSTMCaptioner(BaseCaptioner):
    def __init__(self, image_folder, caption_file, embed_size=256, hidden_size=512, num_layers=1):
        self.transform = transforms.Compose([
            transforms.Resize((224, 224)),
            transforms.ToTensor(),
            transforms.Normalize((0.485, 0.456, 0.406), (0.229, 0.224, 0.225))
        ])
        
        self.dataset = ImageCaptioningDataset(image_folder, caption_file, self.transform)
        self.vocab = self.build_vocab()
        
        self.encoder = EncoderCNN(embed_size)
        self.decoder = DecoderRNN(embed_size, hidden_size, len(self.vocab), num_layers)

    def build_vocab(self):
        # TODO: 실제 구현에서는 캡션 데이터를 기반으로 어휘 사전을 만들어야 합니다.
        return {'<PAD>': 0, '<START>': 1, '<END>': 2, '<UNK>': 3}

    def train(self, num_epochs, batch_size, learning_rate):
        data_loader = DataLoader(self.dataset, batch_size=batch_size, shuffle=True)
        
        criterion = nn.CrossEntropyLoss()
        params = list(self.encoder.parameters()) + list(self.decoder.parameters())
        optimizer = torch.optim.Adam(params, lr=learning_rate)

        for epoch in range(num_epochs):
            for images, captions in data_loader:
                features = self.encoder(images)
                outputs = self.decoder(features, captions)
                loss = criterion(outputs.view(-1, len(self.vocab)), captions.view(-1))
                
                optimizer.zero_grad()
                loss.backward()
                optimizer.step()

            print(f'Epoch [{epoch+1}/{num_epochs}], Loss: {loss.item():.4f}')

    def generate_caption(self, image_input):
        if isinstance(image_input, str):
            image = Image.open(image_input).convert('RGB')
        elif isinstance(image_input, Image.Image):
            image = image_input.convert('RGB')
        else:  # 파일 업로드 객체 처리
            image = Image.open(io.BytesIO(image_input.read())).convert('RGB')
        
        image = self.transform(image).unsqueeze(0)
        
        feature = self.encoder(image)
        
        # 캡션 생성 로직 (실제 구현에서는 beam search 등의 방법을 사용할 수 있습니다)
        caption = []
        with torch.no_grad():
            for i in range(20):  # 최대 20 단어
                if i == 0:
                    hidden = feature.unsqueeze(0)
                    prev_word = torch.tensor([[self.vocab['<START>']]])
                else:
                    prev_word = torch.tensor([[pred_id]])
                
                output, hidden = self.decoder.lstm(prev_word, hidden)
                output = self.decoder.linear(output)
                
                pred_id = output.argmax(2).item()
                word = list(self.vocab.keys())[pred_id]
                caption.append(word)
                
                if word == '<END>':
                    break
        
        return ' '.join(caption[1:-1])  # <START>와 <END> 제거

    def caption_and_translate(self, image_input):
        caption = self.generate_caption(image_input)
        # TODO: 번역 기능 구현 필요
        return json.dumps({
            'original': caption,
            'translated': caption,  # 실제로는 번역된 텍스트로 교체해야 함
            'processing_times': {
                'caption_generation': 0.0,  # 실제 시간 측정 필요
                'translation': 0.0  # 실제 시간 측정 필요
            }
        })

class ImageCaptioningDataset(Dataset):
    def __init__(self, image_folder, caption_file, transform=None):
        self.image_folder = image_folder
        self.transform = transform
        with open(caption_file, 'r') as f:
            self.captions = json.load(f)
        self.imgs = list(self.captions.keys())

    def __len__(self):
        return len(self.imgs)

    def __getitem__(self, idx):
        img_name = self.imgs[idx]
        image = Image.open(os.path.join(self.image_folder, img_name)).convert('RGB')
        if self.transform:
            image = self.transform(image)
        caption = self.captions[img_name]
        return image, caption

class EncoderCNN(nn.Module):
    def __init__(self, embed_size):
        super(EncoderCNN, self).__init__()
        resnet = models.resnet50(pretrained=True)
        for param in resnet.parameters():
            param.requires_grad_(False)
        
        modules = list(resnet.children())[:-1]
        self.resnet = nn.Sequential(*modules)
        self.embed = nn.Linear(resnet.fc.in_features, embed_size)

    def forward(self, images):
        features = self.resnet(images)
        features = features.view(features.size(0), -1)
        features = self.embed(features)
        return features

class DecoderRNN(nn.Module):
    def __init__(self, embed_size, hidden_size, vocab_size, num_layers=1):
        super(DecoderRNN, self).__init__()
        self.embed = nn.Embedding(vocab_size, embed_size)
        self.lstm = nn.LSTM(embed_size, hidden_size, num_layers, batch_first=True)
        self.linear = nn.Linear(hidden_size, vocab_size)

    def forward(self, features, captions):
        embeddings = self.embed(captions[:,:-1])
        embeddings = torch.cat((features.unsqueeze(1), embeddings), 1)
        hiddens, _ = self.lstm(embeddings)
        outputs = self.linear(hiddens)
        return outputs