import sys
from PyQt6.QtWidgets import QApplication, QMainWindow, QVBoxLayout, QWidget, QPushButton, QLineEdit, QTextEdit, QLabel
from PyQt6.QtCore import QThread, pyqtSignal
from playwright.sync_api import sync_playwright
import pandas as pd
import os
import requests
from PIL import Image
from io import BytesIO
import re

class CrawlerThread(QThread):
    update_status = pyqtSignal(str)
    update_result = pyqtSignal(str)

    def __init__(self, keyword):
        super().__init__()
        self.keyword = keyword

    def run(self):
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=False)
            page = browser.new_page()
            page.goto(f'https://search.shopping.naver.com/search/all?bt=-1&frm=NVSCPRO&query={self.keyword}')
            self.update_status.emit("페이지 로드 완료")

            # 스크롤을 통해 Lazy Loading 이미지 로드
            for _ in range(10):
                if page.is_closed():
                    break
                page.mouse.wheel(0, 1000)
                self.sleep(1)

            items = page.query_selector_all('//div[contains(@class, "product_item__")]')
            data = []
            for item in items:
                name_element = item.query_selector('//div[contains(@class, "product_title__")]/a')
                price_element = item.query_selector('//strong[contains(@class, "product_price__")]/span')
                thumbnail_element = item.query_selector('//div[contains(@class, "thumbnail_thumb_wrap__")]/a/img')

                if name_element and price_element and thumbnail_element:
                    name = name_element.inner_text()
                    price = price_element.inner_text()
                    thumbnail_url = thumbnail_element.get_attribute('src')
                    
                    # 파일 이름에서 특수 문자 제거
                    safe_name = re.sub(r'[\\/*?:"<>|]', "", name)
                    
                    # 이미지 다운로드 및 PNG로 저장
                    response = requests.get(thumbnail_url)
                    img = Image.open(BytesIO(response.content))
                    img.save(f'images/{safe_name}.png', 'PNG')
                    
                    data.append([name, price, f'images/{safe_name}.png'])
                    self.update_status.emit(f"상품 크롤링 중: {name}")

            df = pd.DataFrame(data, columns=['Name', 'Price', 'Thumbnail'])
            df.to_excel('products.xlsx', index=False)
            self.update_result.emit("크롤링 완료 및 엑셀 저장 완료")
            browser.close()

class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Naver Shopping Crawler")
        self.setGeometry(100, 100, 800, 600)

        layout = QVBoxLayout()
        self.keyword_input = QLineEdit(self)
        self.keyword_input.setPlaceholderText("키워드를 입력하세요")
        layout.addWidget(self.keyword_input)

        self.start_button = QPushButton("크롤링 시작", self)
        self.start_button.clicked.connect(self.start_crawling)
        layout.addWidget(self.start_button)

        self.status_label = QLabel(self)
        layout.addWidget(self.status_label)

        self.result_text = QTextEdit(self)
        layout.addWidget(self.result_text)

        container = QWidget()
        container.setLayout(layout)
        self.setCentralWidget(container)

    def start_crawling(self):
        keyword = self.keyword_input.text()
        self.crawler_thread = CrawlerThread(keyword)
        self.crawler_thread.update_status.connect(self.update_status)
        self.crawler_thread.update_result.connect(self.update_result)
        self.crawler_thread.start()

    def update_status(self, status):
        self.status_label.setText(status)

    def update_result(self, result):
        self.result_text.append(result)

if __name__ == "__main__":
    if not os.path.exists('images'):
        os.makedirs('images')
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec())