import sys
from PyQt6.QtWidgets import QApplication, QMainWindow, QVBoxLayout, QWidget, QPushButton, QLineEdit, QTextEdit, QLabel
from PyQt6.QtCore import QThread, pyqtSignal
from playwright.sync_api import sync_playwright
import pandas as pd
import os

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
                page.mouse.wheel(0, 1000)
                self.sleep(1)

            items = page.query_selector_all('//div[contains(@class, "product_item__")]')
            data = []
            for item in items:
                name = item.query_selector('//div[contains(@class, "product_title__")]/a').inner_text()
                price = item.query_selector('//strong[contains(@class, "product_price__")]/span').inner_text()
                # ... (다른 정보들 크롤링)
                thumbnail_url = item.query_selector('//div[contains(@class, "thumbnail_thumb_wrap__")]/a/img').get_attribute('src')
                data.append([name, price, thumbnail_url])
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
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec())