import requests
import os
from dotenv import load_dotenv

load_dotenv()

headers = {
    'Authorization': f'Bearer {os.getenv("JINA_API_KEY")}',
    'X-Return-Format': 'text'
}

def extract_text_from_url(url):
    response = requests.get(f"https://r.jina.ai/{url}", headers=headers)
    return response.text