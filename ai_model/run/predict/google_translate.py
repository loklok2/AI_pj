from google.cloud import translate_v2 as translate

class GoogleTranslate:
    def __init__(self):
        self.client = translate.Client()

    def translate_text(self, text, target_language='ko'):
        result = self.client.translate(text, target_language=target_language)
        return result['translatedText']
