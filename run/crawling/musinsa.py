import sys
import os
from PyQt6.QtWidgets import QApplication, QWidget, QVBoxLayout, QHBoxLayout, QLineEdit, QPushButton, QTextEdit, QProgressBar
from PyQt6.QtCore import QThread, pyqtSignal
from playwright.sync_api import sync_playwright
import requests
import time

# 스타일 매핑 추가
STYLE_MAPPING = {
    "traditional": {"클래식": 9, "프레피": 17},
    "manish": {"시티보이": 7},
    "feminine": {"로맨틱": 12, "걸리시": 13},
    "ethnic": {"에스닉": 19},
    "contemporary": {"미니멀": 5},
    "natural": {},
    "genderless": {},
    "sporty": {"고프코어": 20, "스포티": 14},
    "subculture": {"스트릿": 10},
    "casual": {"캐주얼": 8},
}

class CrawlerThread(QThread):
    update_signal = pyqtSignal(str)
    progress_signal = pyqtSignal(int)

    def __init__(self, parent=None):
        super().__init__(parent)
        # keyword 매개변수 제거

    def run(self):
        # 'img' 폴더 생성
        if not os.path.exists('img'):
            os.makedirs('img')

        with sync_playwright() as p:
            browser = p.chromium.launch(headless=False)
            page = browser.new_page()
            base_url = "https://www.musinsa.com/snap/main/recommend?brands=&category=&genders=WOMEN&height-range=&seasons=&styles={}&tpos=&types=&weight-range="
            
            total_styles = sum(len(styles) for styles in STYLE_MAPPING.values())
            current_style = 0

            for folder_name, styles in STYLE_MAPPING.items():
                if not styles:
                    continue

                # 'img' 폴더 안에 스타일 폴더 생성
                style_folder_path = os.path.join('img', folder_name)
                if not os.path.exists(style_folder_path):
                    os.makedirs(style_folder_path)

                for style_name, style_number in styles.items():
                    current_style += 1
                    self.update_signal.emit(f"크롤링 시작: {folder_name} - {style_name}")
                    
                    url = base_url.format(style_number)
                    page.goto(url)
                    page.wait_for_load_state('networkidle')

                    images_downloaded = 0
                    last_height = 0  # 여기에 last_height 초기화

                    while images_downloaded < 100:
                        # 현재 페이지의 이미지 요소를 찾아 리스트에 추가
                        new_images = page.query_selector_all("xpath=//img[contains(@class, 'max-w-full')]")
                        
                        # 새로 발견된 이미지들을 저장
                        for img in new_images:
                            if images_downloaded >= 100:
                                break

                            src = img.get_attribute("src")
                            if src and src.startswith("http") and "basic.png" not in src:
                                try:
                                    img_name = os.path.join(style_folder_path, f"{style_name}_{images_downloaded + 1}.jpg")
                                    if not os.path.exists(img_name):  # 중복 체크
                                        img_data = requests.get(src).content
                                        with open(img_name, "wb") as f:
                                            f.write(img_data)
                                        images_downloaded += 1
                                        self.update_signal.emit(f"이미지 저장됨: {img_name}")
                                        
                                        overall_progress = int((current_style - 1 + images_downloaded / 100) / total_styles * 100)
                                        self.progress_signal.emit(overall_progress)
                                        
                                        if images_downloaded >= 100:
                                            break
                                    time.sleep(0.5)  # 요청 간 딜레이 추가
                                except Exception as e:
                                    self.update_signal.emit(f"이미지 다운로드 실패: {str(e)}")

                        # 스크롤 및 다음 페이지 로딩 로직
                        page.evaluate('window.scrollTo(0, document.body.scrollHeight)')
                        time.sleep(2)  # 스크롤 후 잠시 대기

                        # 새로운 이미지가 로드될 때까지 대기
                        page.wait_for_selector("xpath=//img[contains(@class, 'max-w-full')]", timeout=10000)

                        new_height = page.evaluate('document.body.scrollHeight')
                        if new_height == last_height:
                            # 더 이상 새로운 컨텐츠가 로드되지 않으면 종료
                            break
                        last_height = new_height

                        if images_downloaded >= 100:
                            break

                    self.update_signal.emit(f"{folder_name} - {style_name} 크롤링 완료. {images_downloaded}개의 이미지 저장됨.")

            browser.close()
            self.update_signal.emit("모든 스타일 크롤링 완료.")

class CrawlerGUI(QWidget):
    def __init__(self):
        super().__init__()
        self.initUI()

    def initUI(self):
        layout = QVBoxLayout()

        input_layout = QHBoxLayout()
        self.keyword_input = QLineEdit()
        self.crawl_button = QPushButton("크롤링 시작")
        input_layout.addWidget(self.keyword_input)
        input_layout.addWidget(self.crawl_button)

        self.log_output = QTextEdit()
        self.progress_bar = QProgressBar()

        layout.addLayout(input_layout)
        layout.addWidget(self.log_output)
        layout.addWidget(self.progress_bar)

        self.setLayout(layout)
        self.setWindowTitle("무신사 스냅 크롤러")

        self.crawl_button.clicked.connect(self.start_crawling)

    def start_crawling(self):
        # keyword = self.keyword_input.text()  # 이 줄은 제거 또는 주석 처리
        self.crawler_thread = CrawlerThread()  # keyword 인자 제거
        self.crawler_thread.update_signal.connect(self.update_log)
        self.crawler_thread.progress_signal.connect(self.update_progress)
        self.crawler_thread.start()

    def update_log(self, message):
        self.log_output.append(message)

    def update_progress(self, value):
        self.progress_bar.setValue(value)

if __name__ == "__main__":
    app = QApplication(sys.argv)
    gui = CrawlerGUI()
    gui.show()
    sys.exit(app.exec())