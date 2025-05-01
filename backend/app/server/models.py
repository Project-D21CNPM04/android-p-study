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

    class Config:
        json_schema_extra = {
            "example": {
                "id": "mindmap789",
                "content": "<div class='mindmap'><div class='root'>Central Idea</div><div class='branch'>Topic A</div></div>",
                "summary": "HTML representation of a simple mindmap.",
            }
        }

# Node Schema
class NodeType(str, Enum):
    TEXT = "text"
    LINK = "link"
    FILE = "file"

class Node(BaseModel):
    id: str = Field(..., description="Unique identifier for the node")
    input: str = Field(..., description="Content of the node")
    type: NodeType = Field(..., description="Type of the node (e.g., text, image)")
    user_id: str = Field(..., description="Identifier of the user who created the node")

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

    class Config:
        json_schema_extra = {
            "example": {
                "id": "quiz789",
                "questions": "What is the capital of Vietnam?",
                "choices": ["Hanoi", "Ho Chi Minh City", "Da Nang", "Hue"],
                "answer": "Hanoi",
            }
        }

class TextCreate(BaseModel):
    text: str

class LinkCreate(BaseModel):
    link: str