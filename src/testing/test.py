import os
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = "C:/Users/user/Desktop/AIProject/Data Analysis/img/google_credentials.json"

from google.cloud import translate_v2 as translate

def test_google_translate():
    translate_client = translate.Client()
    result = translate_client.translate("Hello, world!", target_language='ko')
    print(result['translatedText'])

if __name__ == "__main__":
    test_google_translate()
