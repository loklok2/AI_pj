from abc import ABC, abstractmethod

class BaseCaptioner(ABC):
    @abstractmethod
    def generate_caption(self, image):
        pass

    @abstractmethod
    def caption_and_translate(self, image):
        pass