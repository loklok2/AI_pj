from pyngrok import ngrok
import os
from dotenv import load_dotenv
import subprocess

# .env 파일 로드
load_dotenv()

# ngrok 인증 토큰 설정
ngrok_token = os.getenv('NGROK_AUTH_TOKEN')
ngrok.set_auth_token(ngrok_token)

# ngrok 설정
ngrok.kill()  # 기존 프로세스 종료
public_url = ngrok.connect(8501)

print(f'ngrok 터널 생성됨: {public_url}')

# 현재 스크립트의 디렉토리 경로
current_dir = os.path.dirname(os.path.abspath(__file__))

# streamlit.py 파일의 경로
streamlit_path = os.path.join(current_dir, 'streamlit.py')

# Streamlit 앱 실행
subprocess.Popen(["streamlit", "run", streamlit_path])

# 프로그램 종료 대기
input("프로그램을 종료하려면 아무 키나 누르세요...")

# 앱 실행이 끝나면 ngrok 터널 닫기
ngrok.kill()