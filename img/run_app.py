import subprocess
import time
from pyngrok import ngrok, conf

def run_streamlit():
    # app.py를 8502 포트에서 실행
    return subprocess.Popen(["streamlit", "run", "app.py", "--server.port=8502"])

def run_ngrok():
    # ngrok 실행 파일의 경로를 설정합니다
    conf.get_default().ngrok_path = "C:\\Users\\user\\Desktop\\AIProject\\Data Analysis\\ngrok\\ngrok.exe"
    
    # 이전에 열린 ngrok 터널을 모두 종료하여 중복 실행 방지
    ngrok.kill()

    # ngrok을 8502 포트에서 실행
    public_url = ngrok.connect(8502)
    print(f" * ngrok tunnel \"{ public_url }\" -> \"http://127.0.0.1:8502\"")

if __name__ == "__main__":
    streamlit_process = run_streamlit()
    time.sleep(5)    # Streamlit 시작을 기다림
    run_ngrok()      # ngrok도 8502 포트에서 실행

    try:
        # 스크립트를 유지하여 계속 실행
        streamlit_process.wait()
    except KeyboardInterrupt:
        print("Shutting down server.")
        ngrok.kill()
        streamlit_process.terminate()