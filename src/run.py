import subprocess
import time
from pyngrok import ngrok, conf
from config.settings import NGROK_PATH, STREAMLIT_PORT, GOOGLE_CREDENTIALS_PATH
import sys
import os

sys.path.append(os.path.dirname(os.path.abspath(__file__)))
# 현재 스크립트의 디렉토리를 기준으로 상대 경로 설정
current_dir = os.path.dirname(os.path.abspath(__file__))
app_path = os.path.join(current_dir, "app", "main.py")

# 환경 변수 설정
os.environ['GOOGLE_APPLICATION_CREDENTIALS'] = GOOGLE_CREDENTIALS_PATH

def run_streamlit():
    return subprocess.Popen(["python", "-m", "streamlit", "run", app_path, f"--server.port={STREAMLIT_PORT}"])

def run_ngrok():
    conf.get_default().ngrok_path = NGROK_PATH
    ngrok.kill()
    public_url = ngrok.connect(STREAMLIT_PORT)
    print(f" * ngrok tunnel \"{ public_url }\" -> \"http://127.0.0.1:{STREAMLIT_PORT}\"")

if __name__ == "__main__":
    print(f"Attempting to run Streamlit app from: {app_path}")
    streamlit_process = run_streamlit()
    time.sleep(5)
    run_ngrok()

    try:
        streamlit_process.wait()
    except KeyboardInterrupt:
        print("Shutting down server.")
        ngrok.kill()
        streamlit_process.terminate()
