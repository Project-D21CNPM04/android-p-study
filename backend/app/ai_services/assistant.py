from google.generativeai import configure, GenerativeModel
import os
from dotenv import load_dotenv

load_dotenv()

prompt_assistant_cfg = {
    "model_type": "gemini-2.0-flash",
    "api_key": os.getenv("GEMINI_API_KEY")
}

model_type = prompt_assistant_cfg["model_type"]
api_key = prompt_assistant_cfg["api_key"]

SUMMARY_PROMPT = '''Objective: Transform the following text into a comprehensive mindmap:
"{text}"

Mindmap Requirements:
- Clear hierarchical structure with main concepts and subconcepts
- Logical organization of information
- Concise node labels capturing key points
- Appropriate relationship connections between ideas
- Balanced visual representation of information importance

Essential Guidelines:
- Maintain the original meaning and context
- Identify and highlight main themes
- Group related concepts together
- Present information in digestible segments
- Preserve critical details while removing redundancy

Focus on:
- Main topics and subtopics organization
- Relationships between concepts
- Key terms and definitions
- Supporting evidence and examples
- Logical flow of information

Response Requirements:
- Format using markdown with many headings and bullet points
- Output only in markdown format without any closing markdown tag
- No additional comments or explanations
- Present as a diagram in Markdown markup language
- Maintain readability and clarity
- Do NOT include ```markdown closing tag in your response
- Output should end with the last content line, with no closing tags'''

class PromptAssistant:
    def __init__(self):
        self.model = GenerativeModel(model_type.lower())
        self.cfg_model = configure(api_key=api_key)

    def _send_to_model(self, prompt):
        response = self.model.generate_content(
            prompt,
            generation_config={
                "top_k": 32,
                "top_p": 0.95,
                "temperature": 0.7,
                "max_output_tokens": 2048
            }
        )
        return response.text
    
    def summarize_text(self, text):
        prompt = SUMMARY_PROMPT.format(text=text)
        return self._send_to_model(prompt)