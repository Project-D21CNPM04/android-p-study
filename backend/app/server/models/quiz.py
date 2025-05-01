from typing import List
from pydantic import BaseModel, Field

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