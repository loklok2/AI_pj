import sys
import asyncio
import os
from PyQt6.QtWidgets import QApplication, QMainWindow, QVBoxLayout, QPushButton, QLineEdit, QTextEdit, QFileDialog, QWidget
from PyQt6.QtCore import QThread, pyqtSignal
from playwright.async_api import async_playwright
import pandas as pd
import aiohttp
import aiofiles

class CrawlerThread(QThread):
    update_status = pyqtSignal(str)
    update_result = pyqtSignal(dict)

    def __init__(self, keyword):
        super().__init__()
        self.keyword = keyword

    async def download_image(self, session, url, filename):
        async with session.get(url) as response:
            if response.status == 200:
                async with aiofiles.open(filename, mode='wb') as f:
                    await f.write(await response.read())
                return True
        return False

    async def run_crawler(self):
        async with async_playwright() as p:
            browser = await p.chromium.launch(headless=False)
            context = await browser.new_context()
            page = await context.new_page()
            results = []

            async with aiohttp.ClientSession() as session:
                for page_num in range(1, 3):  # 1페이지부터 10페이지까지 크롤링
                    await page.goto(f'https://shop.29cm.co.kr/search?keyword={self.keyword}&sort=RECOMMEND&defaultSort=RECOMMEND&sortOrder=DESC&page={page_num}')
                    await page.wait_for_selector('li.css-1vv8hji')

                    items = await page.query_selector_all('li.css-1vv8hji')

                    for item in items:
                        name_element = await item.query_selector('h5.css-up5jpd')
                        price_element = await item.query_selector('strong.css-746sl9')
                        seller_count_element = await item.query_selector('div.css-1fhgjcy')
                        options_element = await item.query_selector('ul.css-vnsjw9')
                        rating_element = await item.query_selector('span.rating')
                        review_count_element = await item.query_selector('span.review')
                        thumbnail_element = await item.query_selector('img')
                        shipping_fee_element = await item.query_selector('li.css-i7zp1f')

                        name = await name_element.inner_text() if name_element else ''
                        price = await price_element.inner_text() if price_element else ''
                        seller_count = await seller_count_element.inner_text() if seller_count_element else ''
                        options = await options_element.inner_text() if options_element else ''
                        rating = await rating_element.inner_text() if rating_element else ''
                        review_count = await review_count_element.inner_text() if review_count_element else ''
                        thumbnail_url = await thumbnail_element.get_attribute('src') if thumbnail_element else ''
                        shipping_fee = await shipping_fee_element.inner_text() if shipping_fee_element else ''

                        result = {
                            'name': name,
                            'price': price,
                            'seller_count': seller_count,
                            'options': options,
                            'rating': rating,
                            'review_count': review_count,
                            'thumbnail_url': thumbnail_url,
                            'shipping_fee': shipping_fee
                        }

                        results.append(result)
                        self.update_result.emit(result)

                        # Ensure the images directory exists
                        if not os.path.exists('images'):
                            os.makedirs('images')

                        # 썸네일 이미지 처리
                        if thumbnail_url:
                            try:
                                # 원본 이미지 URL 추출
                                original_url = thumbnail_url.replace('?q=80&w=320', '')
                                
                                # 파일명 생성
                                valid_name = "".join([c if c.isalnum() or c in (' ', '.', '_') else '_' for c in name])
                                img_path = f'images/{valid_name}.jpg'
                                
                                # 이미지 다운로드
                                if await self.download_image(session, original_url, img_path):
                                    self.update_status.emit(f'고품질 이미지 저장됨: {img_path}')
                                else:
                                    self.update_status.emit(f'이미지 다운로드 실패: {original_url}')
                            except Exception as e:
                                self.update_status.emit(f'이미지 저장 중 오류 발생: {str(e)}')

            await browser.close()
            return results

    def run(self):
        asyncio.run(self.run_crawler())

class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle('Product Crawler')
        self.setGeometry(100, 100, 800, 600)

        layout = QVBoxLayout()

        self.keyword_input = QLineEdit(self)
        self.keyword_input.setPlaceholderText('Enter keyword')
        layout.addWidget(self.keyword_input)

        self.start_button = QPushButton('Start Crawling', self)
        self.start_button.clicked.connect(self.start_crawling)
        layout.addWidget(self.start_button)

        self.status_text = QTextEdit(self)
        self.status_text.setReadOnly(True)
        layout.addWidget(self.status_text)

        self.export_button = QPushButton('Export to Excel', self)
        self.export_button.clicked.connect(self.export_to_excel)
        layout.addWidget(self.export_button)

        container = QWidget()
        container.setLayout(layout)
        self.setCentralWidget(container)

        self.crawler_thread = None
        self.results = []

    def start_crawling(self):
        keyword = self.keyword_input.text()
        if keyword:
            self.crawler_thread = CrawlerThread(keyword)
            self.crawler_thread.update_status.connect(self.update_status)
            self.crawler_thread.update_result.connect(self.update_result)
            self.crawler_thread.start()

    def update_status(self, status):
        self.status_text.append(status)

    def update_result(self, result):
        self.results.append(result)
        self.status_text.append(str(result))

    def export_to_excel(self):
        if self.results:
            df = pd.DataFrame(self.results)
            default_path = os.path.join(os.getcwd(), "Product_Crawler_Results.xlsx")
            file_path, _ = QFileDialog.getSaveFileName(self, "Save File", default_path, "Excel Files (*.xlsx)")
            if file_path:
                if not file_path.endswith('.xlsx'):
                    file_path += '.xlsx'
                df.to_excel(file_path, index=False)
                self.status_text.append(f'Results exported to {file_path}')
            else:
                self.status_text.append('Export cancelled or invalid file path.')

if __name__ == '__main__':
    app = QApplication(sys.argv)
    window = MainWindow()
    window.show()
    sys.exit(app.exec())