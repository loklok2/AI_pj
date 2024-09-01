import os
import glob
import json
from itertools import chain

import torch
from tqdm import tqdm
import numpy as np
from torch.utils.data import Dataset, DataLoader
import torchvision.transforms.functional as TF
from torchvision import transforms

from PIL import Image, ImageDraw, ImageFile
ImageFile.LOAD_TRUNCATED_IMAGES = True
device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')

# "카테코리" single label but make into multilabel (explained below)
CLOTHING_CATEGORIES = {
    "상의": ["탑", "블라우스", "티셔츠", "니트웨어", "셔츠", "브라탑", "후드티"],
    "하의": ["청바지", "팬츠", "스커트", "래깅스", "조거팬츠"],
    "아우터": ["코트", "재킷", "점퍼", "패딩", "베스트", "가디건", "짚업"],
    "원피스": ["드레스", "점프수트"]
}
# including the keys because some data doesnt have the specific clothing item tag
# MULTILABEL
CLOTHING_CATEGORIES = list(chain(*list(CLOTHING_CATEGORIES.values()))) + list(CLOTHING_CATEGORIES.keys()) 
# print("CLOTHING_CATEGORIES", CLOTHING_CATEGORIES)
# MULTILABEL
MATERIAL_CATEGORIES = ["패딩", "퍼", "무스탕", "스웨이드", "앙고라", "코듀로이", "시퀸/글리터", "데님", "저지", "트위드", "벨벳", "비닐/PVC", "울/캐시미어", "합성섬유", "헤어 니트", "니트", "레이스", "린넨", "메시", "플리스", "네오프렌", "실크", "스판덱스", "자카드", "가죽", "면", "시폰", "우븐"]

STYLE_CATEGORIES = {
    "클래식": ["클래식", "프레피"],
    "매니시": ["매니시", "톰보이"],
    "엘레강스": ["엘레강스", "소피스케이티드", "글래머러스"],
    "에스닉": ["에스닉", "히피", "오리엔탈"],
    "모던": ["모던", "미니멀"],
    "내추럴": ["내추럴", "컨트리", "리조트"],
    "로맨틱": ["로맨틱", "섹시"],
    "스포티": ["스포티", "애슬레져", "밀리터리"],
    "문화": ["뉴트로", "힙합", "키티/키덜트", "맥시멈", "펑크/로커"],
    "캐주얼": ["캐주얼", "놈코어"]
}
# "기장" - single label
SLEEVE_CATEGORIES = {
    "상의": ["크롭", "노멀", "롱"],
    "하의": ["미니", "니렝스", "미디", "발목", "맥시"],
    "아우터": ["크롭", "노멀", "하프", "롱", "맥시"],
    "원피스": ["미니", "니렝스", "미디", "발목", "맥시"],
}
SLEEVE_CATEGORIES = list(set(chain(*list(SLEEVE_CATEGORIES.values()))))

# "소매기장" - single label
SHIRT_SLEEVES = ["없음", "민소매", "반팔", "캡", "7부소매", "긴팔"]


# "넥라인" - single label
NECKLINE_CATEGORIES = ["라운드넥", "유넥", "브이넥", "홀토넥", "오프숄더", "원 숄더", "스퀘어넥", "노카라", "후드", "터틀넥", "보트넥", "스위트하트"]

# "옷깃" - single label
COLLAR_CATEGORIES = ["셔츠칼라", "보우칼라", "세일러칼라", "숄칼라", "폴로칼라", "피터팬칼라", "너치드칼라", "차이나칼라", "테일러칼라", "밴드칼라"]

# "핏" - single label
FIT_CATEGORIES = ["노멀", "루즈", "오버사이즈", "스키니", "와이드", "타이트", "벨보텀"]



# 이미지를 로드하고 바운딩 박스를 저장하는 함수
def saveimg_bbox(image, name, box):
    if not isinstance(box, list):
        box = box.detach().cpu().numpy()
    np_img = np.uint8(image.cpu().numpy() * 255)
    np_img = np_img.transpose(1, 2, 0)
    img = Image.fromarray(np_img, mode="RGB")
    
    img1 = ImageDraw.Draw(img)  
    img1.rectangle(box, outline="red")
    img.save(name)

# Transform 설정
def get_transform(train):
    selected_transforms = []
    selected_transforms.append(transforms.ToTensor())
    if train:
        selected_transforms.append(transforms.RandomHorizontalFlip(0.5))
    return transforms.Compose(selected_transforms)

# 박스 면적 계산 함수
def box_area(box):
    width = box[2] - box[0]
    height = box[3] - box[1]
    return width * height

# 데이터 추출 함수
def extract_data(one_labels):
    ID = one_labels["이미지 정보"]["이미지 식별자"]
    height = one_labels["이미지 정보"]["이미지 높이"]
    width = one_labels["이미지 정보"]["이미지 너비"]

    all_rects = one_labels["데이터셋 정보"]["데이터셋 상세설명"]["렉트좌표"]
    styles = one_labels["데이터셋 정보"]["데이터셋 상세설명"]["라벨링"]
    overall_style = list([one["스타일"] for one in styles["스타일"] if len(one.values()) > 0])
    if len(overall_style) == 0:
        overall_style = ["기타"]

    all_clothing_categories = list(all_rects.keys())
    
    rects = []
    clothing_categories = []
    for rect_idx, rect in enumerate(all_rects.values()):
        for one_rect in rect:
            if len(one_rect.keys()) > 0:
                assert list(one_rect.keys()) == ["X좌표", "Y좌표", "가로", "세로"]
                box = list(one_rect.values())
                box[2] = box[0] + box[2]
                box[3] = box[1] + box[3]
                area = box_area(box)
                if area > 1:        
                    rects.append(box)
                    clothing_categories.append(all_clothing_categories[rect_idx])
                break

    boxes = [] 
    all_attributes = {
        "material": [],
        "fit": [],
        "collar": [],
        "neckline": [],
        "shirt_sleeve": [],
        "sleeve": [],
        "clothing_categories": []
    }

    if len(clothing_categories) == 0:
        return None

    for clothing_idx, cat in enumerate(clothing_categories):
        rect = rects[clothing_idx]
        boxes.append(rect)

        one_clothing_attributes = styles[cat][0]
        for attribute_name, attribute_value in one_clothing_attributes.items():
            if attribute_name == "카테고리":
                all_attributes["clothing_categories"].append(attribute_value)
            elif attribute_name == "기장":
                all_attributes["sleeve"].append(attribute_value)
            elif attribute_name == "소매기장":
                all_attributes["shirt_sleeve"].append(attribute_value)
            elif attribute_name == "핏":
                all_attributes["fit"].append(attribute_value)
            elif attribute_name == "소재":
                all_attributes["material"].append(attribute_value)
            elif attribute_name == "칼라":
                all_attributes["collar"].append(attribute_value)
    
        for key, value in all_attributes.items():
            if len(value) <= clothing_idx:
                if key == "카테고리":
                    all_attributes[key].append(clothing_categories[clothing_idx])
                else:
                    all_attributes[key].append(0)

    obj = {
        "ID": ID,
        "overall_style": overall_style,
        "height": height,
        "width": width,
        "boxes": boxes,
        "labels": clothing_categories,
        "attributes": all_attributes
    }
    return obj

# 데이터 원핫 인코딩 함수
def extract_to_onehot(extracted_obj):
    attr = extracted_obj["attributes"]

    attribute_dict = {}

    multi_label_keys = {
        "material": MATERIAL_CATEGORIES,
    }
    single_label_keys = {
        "fit" : FIT_CATEGORIES,
        "collar": COLLAR_CATEGORIES, 
        "neckline": NECKLINE_CATEGORIES, 
        "shirt_sleeve": SHIRT_SLEEVES, 
        "sleeve": SLEEVE_CATEGORIES,
    }

    clothing = attr["clothing_categories"]
    for idx, label in enumerate(clothing):
        clothing_super = extracted_obj["labels"][idx]
        if label == 0:
            clothing[idx] = clothing_super
        else:
            clothing[idx] = label
        
    clothing = [CLOTHING_CATEGORIES.index(one) for one in clothing]
    extracted_obj["labels"] = clothing

    for multi_label_key, corres_lookup in multi_label_keys.items(): 
        multi_attr = attr[multi_label_key]
        for idx, one_box_attr in enumerate(multi_attr):
            new_arr = [0] * (len(corres_lookup) + 1)
            if one_box_attr == 0:
                new_arr[0] = 1
            else:
                for one_hot_idx in one_box_attr:
                    if one_hot_idx != 0:
                        try:
                            one_hot_idx = corres_lookup.index(one_hot_idx) + 1
                        except:
                            print(one_hot_idx, multi_label_key, corres_lookup)
                            raise Exception()
                    new_arr[one_hot_idx] = 1
            
            multi_attr[idx] = new_arr
       
        attribute_dict[multi_label_key] = torch.tensor(multi_attr, dtype=torch.float32).to(device)
    
    for single_label_key, corres_lookup in single_label_keys.items():
        single_attr = attr[single_label_key]
        for idx, one_box_attr in enumerate(single_attr):
            if one_box_attr != 0:
                if one_box_attr == "노말":
                    one_box_attr = "노멀"
                try:
                    one_box_attr = corres_lookup.index(one_box_attr) + 1
                except:
                    print(one_box_attr, single_label_key, corres_lookup)
                    raise Exception()
            single_attr[idx] = one_box_attr
        attribute_dict[single_label_key] = torch.tensor(single_attr, dtype=torch.int64).to(device)
    
    extracted_obj["attributes"] = attribute_dict

    return extracted_obj

# 데이터셋 클래스
class KFashionDataset(Dataset):
    def __init__(self, train, data_root, selected_transforms=None):
        data_root = data_root + "/" if not data_root.endswith("/") else data_root
        if train:
            json_files = list(glob.glob(data_root + "Training/라벨링데이터/*/*.json"))
        else:
            json_files = list(glob.glob(data_root + "Validation/라벨링데이터/기타/*.json"))

        images = []
        dataset = []

        for jfile in tqdm(json_files, desc="Loading images and processing data"):
            with open(jfile, "r", encoding="utf-8") as jopen:
                full_data = json.load(jopen)
            
            extracted = extract_data(full_data)
            if extracted is not None:
                processed = extract_to_onehot(extracted)

                ID = extracted["ID"]
                if train:
                    image_path = list(glob.glob(data_root + f"Training/원천데이터/**/*/{ID}.jpg", recursive=True))
                else:
                    image_path = list(glob.glob(data_root + f"Validation/원천데이터/**/*/{ID}.jpg", recursive=True))
                assert len(image_path) == 1

                images.append(image_path[0])  
                dataset.append(processed)
              
        self.images = images
        self.dataset = dataset
        if selected_transforms is None:
            self.transforms = get_transform(train)
        else: 
            self.transforms = selected_transforms

        print(f"Total images loaded: {len(self.images)}")

    def __len__(self):
        return len(self.dataset)

    def __getitem__(self, idx):        
        image_path = self.images[idx]
        pil_image = Image.open(image_path)
        image = self.transforms(pil_image)
        pil_image.close()
        target = self.dataset[idx]
        target = {key: value.to(device) for key, value in target.items()}
        return (image, target)

# 전처리 후 데이터 저장
def preprocess_and_save_dataset(dataset, save_dir):
    os.makedirs(save_dir, exist_ok=True)
    
    for idx in tqdm(range(len(dataset)), desc="Saving preprocessed data"):
        image, target = dataset[idx]
        save_path = os.path.join(save_dir, f"data_{idx}.pt")
        torch.save((image, target), save_path)
    
    print(f"Preprocessed dataset saved to {save_dir}")

# 미리 처리된 데이터셋 클래스
class PreloadedKFashionDataset(torch.utils.data.Dataset):
    def __init__(self, data_dir):
        self.data_dir = data_dir
        self.data_files = sorted(os.listdir(data_dir))
    
    def __len__(self):
        return len(self.data_files)
    
    def __getitem__(self, idx):
        data_path = os.path.join(self.data_dir, self.data_files[idx])
        image, target = torch.load(data_path)
        return image, target

# 배치 데이터 전처리
def collate_fn(batch):
    images = [one[0] for one in batch]
    targets = [one[1] for one in batch]
    boxes = [one["boxes"] for one in targets]

    height = max([one.size(1) for one in images])
    width = max([one.size(2) for one in images])

    new_images = []
    for image_idx, image in enumerate(images):
        one_boxes = boxes[image_idx]

        c, cur_height, cur_width = image.size()

        one_boxes = torch.tensor(one_boxes)
        
        height_pad = height - cur_height
        width_pad = width - cur_width

        width_diff = 0 if (width_pad / 2) % 1 == 0 else 1
        height_diff = 0 if (height_pad / 2) % 1 == 0 else 1

        padding = (width_pad // 2 + width_diff, height_pad // 2 + height_diff, width_pad // 2, height_pad // 2)
        image = TF.pad(image, padding=padding, padding_mode="constant", fill=0)

        box_padding = (width_pad // 2 + width_diff, height_pad // 2 + height_diff, width_pad // 2 + width_diff, height_pad // 2 + height_diff)
        one_boxes = torch.add(one_boxes, torch.tensor(box_padding))
        
        targets[image_idx]["boxes"] = one_boxes

        for key in ["overall_style", "height", "width"]:
            if key in targets[image_idx]:
                del targets[image_idx][key]
        
        new_images.append(image)

    images = torch.stack(new_images, dim=0)
    return (images, targets)

# 데이터 로더
def load_data(train, batch_size=4, num_workers=4, data_root="./"):
    save_dir = './preprocessed_data/train' if train else './preprocessed_data/val'
    if os.path.exists(save_dir) and len(os.listdir(save_dir)) > 0:
        print(f"Loading preprocessed dataset from {save_dir}...")
        dataset = PreloadedKFashionDataset(save_dir)
    else:
        print("Preprocessed dataset not found. Creating new dataset...")
        dataset = KFashionDataset(train, data_root=data_root)
        preprocess_and_save_dataset(dataset, save_dir)
    dataloader = DataLoader(dataset, batch_size=batch_size, shuffle=True, num_workers=num_workers, collate_fn=collate_fn)
    return dataloader