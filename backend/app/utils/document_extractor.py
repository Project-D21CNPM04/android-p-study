import io
import re
import os
import tempfile
import PyPDF2
import docx2txt
import docx2pdf

class DocumentExtractor:
    def __init__(self):
        pass
        
    def read_pdf(self, file_path):
        try:           
            text_parts = []
            with open(file_path, 'rb') as file:
                reader = PyPDF2.PdfReader(file)
                for page_num in range(len(reader.pages)):
                    page = reader.pages[page_num]
                    text_parts.append(page.extract_text())
            
            text = ' '.join(text_parts)
            text = re.sub(r'\s+', ' ', text)
            text = re.sub(r'\n+', ' ', text)       
            return text.strip()
        except Exception as e:
            return f"Error reading PDF file: {str(e)}"

    def read_doc_docx(self, file_path):
        try:        
            text = docx2txt.process(file_path)
            text = re.sub(r'\s+', ' ', text)
            text = re.sub(r'\n+', ' ', text)           
            return text.strip()
        except Exception as e:
            return f"Error reading DOC/DOCX file: {str(e)}"

    def read_doc_using_docx2pdf(self, file_path):
        try:           
            temp_dir = tempfile.gettempdir()
            temp_docx = os.path.join(temp_dir, "temp_converted.docx")
            docx2pdf.convert(file_path, temp_docx)
            text = self.read_doc_docx(temp_docx)
            if os.path.exists(temp_docx):
                os.remove(temp_docx)            
            return text
        except Exception as e:
            return f"Error reading DOC file: {str(e)}"

    def extract_text_from_document(self, file_path):
        file_ext = file_path.lower().split('.')[-1]
        if file_ext == 'pdf':
            return self.read_pdf(file_path)
        elif file_ext == 'docx':
            return self.read_doc_docx(file_path)
        elif file_ext == 'doc':
            result = self.read_doc_docx(file_path)
            if not result.startswith("Error"):
                return result
            return self.read_doc_using_docx2pdf(file_path)
        else:
            return f"Unsupported file format: {file_ext}. Supported formats are PDF, DOC, and DOCX."
