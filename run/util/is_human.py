import cv2
import numpy as np
from ultralytics import YOLO
from PIL import Image
from datetime import datetime
import os

class HumanDetector:
    def __init__(self, model_path='yolov8n.pt'):
        self.model = YOLO(model_path)
        self.face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

    def detect_and_execute(self, image):
        # PIL Image를 numpy 배열로 변환
        img_array = np.array(image)
        results = self.model(img_array)

        person_detected = self._check_person(results)

        if not person_detected:
            return False
        else:
            return self._process_person(img_array, results)

    def _check_person(self, results):
        for box in results[0].boxes:
            if self.model.names[int(box.cls)] == "person":
                return True
        return False

    def _process_person(self, img, results):
        for box in results[0].boxes:
            if self.model.names[int(box.cls)] == "person":
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                person_img = img[y1:y2, x1:x2]
                person_img_without_face = self._remove_face(person_img)
                return Image.fromarray(person_img_without_face)
        return False

    def _remove_face(self, person_img):
        gray = cv2.cvtColor(person_img, cv2.COLOR_BGR2GRAY)
        faces = self.face_cascade.detectMultiScale(gray, 1.3, 5)
        for (x, y, w, h) in faces:
            cv2.rectangle(person_img, (x, y), (x+w, y+h), (255, 255, 255), -1)
        return cv2.cvtColor(person_img, cv2.COLOR_BGR2RGB)

# 사용 예시는 제거했습니다.