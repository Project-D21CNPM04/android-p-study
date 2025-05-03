import assemblyai as aai
import os
from dotenv import load_dotenv

load_dotenv()

class AudioAssistant:
    def __init__(self):
        self.api_key = os.getenv("ASSEMBLYAI_API_KEY")
        aai.settings.api_key = self.api_key
        
    def transcribe_audio(self, audio_file_path):
        try:
            config = aai.TranscriptionConfig(speech_model=aai.SpeechModel.best)
            transcript = aai.Transcriber(config=config).transcribe(audio_file_path)
            
            if transcript.status == "error":
                raise RuntimeError(f"Transcription failed: {transcript.error}")
                
            return transcript.text
        except Exception as e:
            return f"Error transcribing audio file: {str(e)}"