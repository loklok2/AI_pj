# 사용방법
1. 깃에서 내려받기
2. https://drive.google.com/drive/folders/10rADAkLmDwqO2D5Qgt8k31sRuDiCMBk_?usp=sharing에서 checkpoint와 csv파일 다운받기
    - csv파일은 db폴더에, checkpoint는 루트 폴더에 넣기
3. pip install -r requirements.txt
4. python run\web\app.py
5. ngrok(모델 검증용 외부 접속 사이트) 사용하려면 ngrok_token을 발급받아서 run\web\ngrok.py의 값에 입력하여 python run\web\ngrok.py실행