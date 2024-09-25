import os
import time
import requests
import pandas as pd
import threading
from playwright.sync_api import sync_playwright
class CrawlerThread(threading.Thread):
    def __init__(self, keyword, save_folder, item_nums=100):
        super().__init__()
        self.keyword = "여성 " + keyword
        self.save_folder = save_folder
        self.results = []
        self.item_nums = item_nums
        self.progress = 0
        self.status = ""
        self.df = None  # df 변수 추가

    def run(self):
        try:
            os.makedirs(self.save_folder, exist_ok=True)
            with sync_playwright() as p:
                browser = p.chromium.launch(headless=False)
                page = browser.new_page()
                base_url = "https://search.shopping.naver.com/search/all"
                current_page = 1

                while len(self.results) < self.item_nums:
                    url = f"{base_url}?query={self.keyword}&pagingIndex={current_page}&pagingSize=40&productSet=total&sort=review&timestamp=&viewType=list"
                    page.goto(url, wait_until="domcontentloaded")
                    self.status = f"페이지 {current_page} 로드 완료"
                    print(self.status)

                    self.scroll_page(page)

                    items = page.query_selector_all("div.product_item__MDtDF")
                    self.status = f"검색된 아이템 수: {len(items)}"
                    print(self.status)

                    for item in items:
                        if len(self.results) >= self.item_nums:
                            break
                        try:
                            name = item.query_selector("a.product_link__TrAac")
                            price = item.query_selector("span.price_num__S2p_v")
                            seller_url_element = item.query_selector("a.thumbnail_thumb__Bxb6Z.linkAnchor._nlog_click._nlog_impression_element")
                            seller_url = seller_url_element.get_attribute("href") if seller_url_element else "N/A"
                            rating = item.query_selector("div.product_info_area__xxCTi span.product_grade__IzyU3")
                            review_count = item.query_selector("div.product_info_area__xxCTi em.product_num__fafe5")
                            thumbnail = item.query_selector("div.thumbnail_thumb_wrap__RbcYO img")

                            if name and price:
                                name = name.inner_text()
                                price = price.inner_text().replace("원","").strip()
                                rating = rating.inner_text().replace("별점", "").strip() if rating else "N/A"
                                review_count = review_count.inner_text() if review_count else "N/A"
                                thumbnail_url = thumbnail.get_attribute("src").replace("?type=f140", "") if thumbnail else "N/A"
                                saved_url = os.path.join(self.save_folder, f"thumbnail_{len(self.results)}.jpg").replace("run/web/", "http://10.125.121.187:5000/")

                                if thumbnail_url != "N/A":
                                    self.download_image(thumbnail_url, os.path.join(self.save_folder, f"thumbnail_{len(self.results)}.jpg"))
                                self.results.append({
                                    "name": name,
                                    "price": price,
                                    "rating": rating,
                                    "review_count": review_count,
                                    "seller_url": seller_url,
                                    "pimgPath" : saved_url
                                })

                            self.progress = int(len(self.results) / self.item_nums * 100)
                            self.status = f"아이템 {len(self.results)} 수집 완료"
                            print(self.status)
                        except Exception as e:
                            print(f"아이템 처리 중 오류 발생: {str(e)}")

                    current_page += 1
                    if len(items) == 0:
                        print("더 이상 아이템이 없습니다.")
                        break

                browser.close()

            self.df = self.export_to_csv()  # df를 인스턴스 변수에 저장
            print("크롤링 완료")
        except Exception as e:
            print(f"크롤링 중 오류 발생: {str(e)}")

    def scroll_page(self, page):
        """페이지를 아래로 스크롤하고, 요소가 로딩될 때까지 기다림"""
        for _ in range(10):
            page.evaluate("window.scrollBy(0, window.innerHeight)")
            time.sleep(1)  # 스크롤 후 약간의 시간 대기
            
            # 'rating' 요소가 나타날 때까지 기다리기
            # try:
            #     page.wait_for_function(
            #         "document.querySelector('div.product_info_area__xxCTi span.product_grade__IzyU3') && document.querySelector('div.product_info_area__xxCTi span.product_grade__IzyU3').innerText.trim() !== ''",
            #         timeout=5000  # 최대 5초 대기
            #     )
            #     print("별점 요소 로딩 완료")
            # except Exception as e:
            #     print("별점 요소 로딩 실패: " + str(e))


    def download_image(self, url, filename):
        """이미지 다운로드"""
        try:
            response = requests.get(url)
            if response.status_code == 200:
                with open(filename, 'wb') as f:
                    f.write(response.content)
            else:
                print(f"이미지 다운로드 실패: {url}, 상태 코드: {response.status_code}")
        except Exception as e:
            print(f"이미지 다운로드 중 오류 발생: {str(e)}")

    def export_to_csv(self):
        """크롤링한 데이터를 CSV로 내보내고 DataFrame 반환"""
        if self.results:
            try:
                df = pd.DataFrame(self.results)
                df.to_csv(os.path.join(self.save_folder, "naver_shopping_results.csv"), index=False)
                print("CSV 파일로 내보내기 완료")
                return df  # DataFrame 반환
            except Exception as e:
                print(f"CSV 내보내기 중 오류 발생: {str(e)}")
                return None  # 오류 발생 시 None 반환
        return None  # 결과가 없을 경우 None 반환
