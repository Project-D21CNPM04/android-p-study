import base64
from io import BytesIO
from PIL import Image

def extract_base64_from_image(file_content, format='JPEG'):
    try:
        img = Image.open(BytesIO(file_content))
        if format.upper() == 'JPEG' and img.mode != 'RGB':
            img = img.convert('RGB')
        
        buffered = BytesIO()
        img.save(buffered, format=format)
        encoded_img = base64.b64encode(buffered.getvalue()).decode('utf-8')
        return encoded_img
    except Exception as e:
        return f"Error processing image: {str(e)}"