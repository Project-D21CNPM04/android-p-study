from pydantic import BaseModel, Field
from enum import Enum
from typing import List, Optional

class FlashcardContent(BaseModel):
    front: str
    back: str

class Flashcard(BaseModel):
    id: str = Field(..., description="Unique identifier for the flashcard")
    title: str
    content: FlashcardContent
    note_id: str

    class Config:
        json_schema_extra = {
            "example": {
                "id": "flashcard123",
                "title": "Vocabulary - Colors",
                "content": {"front": "Red", "back": "Đỏ"},
            }
        }

class Mindmap(BaseModel):
    id: str = Field(..., description="Unique identifier for the mindmap")
    content: str = Field(..., description="HTML content representing the mindmap")
    summary: Optional[str] = Field(None, description="Optional summary of the mindmap")
    note_id: str

    class Config:
        json_schema_extra = {
            "example": {
                "id": "mindmap789",
                "content": "<div class='mindmap'><div class='root'>Central Idea</div><div class='branch'>Topic A</div></div>",
                "summary": "HTML representation of a simple mindmap.",
            }
        }

class NoteType(str, Enum):
    TEXT = "text"
    LINK = "link"
    FILE = "file"

class Note(BaseModel):
    id: str = Field(..., description="Unique identifier for the note")
    input: str = Field(..., description="Content of the note")
    type: NoteType = Field(..., description="Type of the note (e.g., text, image)")
    user_id: str = Field(..., description="Identifier of the user who created the note")

    class Config:
        json_schema_extra = {
            "example": {
                "id": "node101",
                "input": "Hello World!",
                "type": "text",
                "user_id": "user123",
            }
        }

class Quiz(BaseModel):
    id: str = Field(..., description="Unique identifier for the quiz")
    questions: str
    choices: List[str]
    answer: str
    note_id: str

    class Config:
        json_schema_extra = {
            "example": {
                "id": "quiz789",
                "questions": "What is the capital of Vietnam?",
                "choices": ["Hanoi", "Ho Chi Minh City", "Da Nang", "Hue"],
                "answer": "Hanoi",
            }
        }
        
class Summary(BaseModel):
    id: str = Field(..., description="Unique identifier for the summary")
    content: str = Field(..., description="HTML content of the summary")
    note_id: str

    class Config:
        json_schema_extra = {
            "example": {
                "id": "summary456",
                "content": "Nguyen Huu Quang Hoa is a excellent student!",
                "note_id": "note123"
            }
        }

class TextCreate(BaseModel):
    text: str

class LinkCreate(BaseModel):
    link: str