from pydantic import BaseModel, Field

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