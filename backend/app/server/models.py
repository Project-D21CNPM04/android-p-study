from pydantic import BaseModel, Field, HttpUrl
from enum import Enum
from typing import List, Optional, Dict, Union
import time

class NoteType(str, Enum):
    TEXT = "text"
    LINK = "link"
    FILE = "file"
    AUDIO = "audio"
    IMAGE = "image"

class DifficultyLevel(int, Enum):
    EASY = 1
    MEDIUM = 2
    HARD = 3
    MIXED = 4

# User authentication models
class UserLogin(BaseModel):
    email: str = Field(..., description="User email address")
    password: str = Field(..., description="User password")

class UserResponse(BaseModel):
    email: str = Field(..., description="User email address")
    token: str = Field(..., description="Authentication token")

# Dashboard statistics models
class HourlyStats(BaseModel):
    name: str = Field(..., description="Hour of the day (e.g. '8:00')")
    value: int = Field(..., description="Number of items created in this hour")

class DailyStats(BaseModel):
    name: str = Field(..., description="Day of the week")
    value: int = Field(..., description="Number of items created on this day")

class TypeStats(BaseModel):
    name: str = Field(..., description="Type of content")
    value: int = Field(..., description="Number of items of this type")

class UserNoteStats(BaseModel):
    name: str = Field(..., description="User name")
    notes: int = Field(..., description="Number of notes created by this user")

class NotesStatistics(BaseModel):
    total: int = Field(..., description="Total number of notes")
    byHour: List[HourlyStats] = Field(..., description="Notes created by hour of day")
    byDay: List[DailyStats] = Field(..., description="Notes created by day of week")
    byType: List[TypeStats] = Field(..., description="Notes created by content type")

class ContentTypeStatistics(BaseModel):
    total: int = Field(..., description="Total number of items")
    byType: List[TypeStats] = Field(..., description="Items created by content type")

class UserStatistics(BaseModel):
    total: int = Field(..., description="Total number of users")
    notesPerUser: List[UserNoteStats] = Field(..., description="Notes created per user")
    maxNotes: int = Field(..., description="Maximum notes created by a user")
    minNotes: int = Field(..., description="Minimum notes created by a user")
    avgNotes: int = Field(..., description="Average notes created per user")

class DashboardStatistics(BaseModel):
    notes: NotesStatistics = Field(..., description="Statistics about notes")
    quiz: ContentTypeStatistics = Field(..., description="Statistics about quizzes")
    flashcard: ContentTypeStatistics = Field(..., description="Statistics about flashcards")
    mindmap: ContentTypeStatistics = Field(..., description="Statistics about mindmaps")
    users: UserStatistics = Field(..., description="Statistics about users")

class FlashcardContent(BaseModel):
    front: str = Field(..., description="Text displayed on the front side of the flashcard")
    back: str = Field(..., description="Text displayed on the back side of the flashcard")

class Flashcard(BaseModel):
    id: str = Field(..., description="Unique identifier for the flashcard")
    title: str = Field(..., description="Title or topic of the flashcard")
    content: FlashcardContent = Field(..., description="Content structure with front and back text")
    note_id: str = Field(..., description="Reference to the parent note")

    class Config:
        json_schema_extra = {
            "example": {
                "id": "f2a7b9e4-6d3c-4e5f-8g1h-2i3j4k5l6m7n",
                "title": "Biology - Cell Structure",
                "content": {
                    "front": "What is the powerhouse of the cell?", 
                    "back": "Mitochondria"
                },
                "note_id": "n7m6l5k4-j3i2-h1g0-f9e8-d7c6b5a4321"
            }
        }

class Mindmap(BaseModel):
    id: str = Field(..., description="Unique identifier for the mindmap")
    content: str = Field(..., description="HTML content representing the mindmap structure")
    summary: Optional[str] = Field(None, description="Brief text summary of the mindmap content")
    note_id: str = Field(..., description="Reference to the parent note")

    class Config:
        json_schema_extra = {
            "example": {
                "id": "a1b2c3d4-e5f6-g7h8-i9j0-k1l2m3n4o5p6",
                "content": "<div class='mindmap'><div class='node root'>Biology</div><div class='node child'>Cell Structure</div></div>",
                "summary": "Overview of basic biological concepts focusing on cellular structures and functions",
                "note_id": "n7m6l5k4-j3i2-h1g0-f9e8-d7c6b5a4321"
            }
        }

class Note(BaseModel):
    id: str = Field(..., description="Unique identifier for the note")
    input: str = Field(..., description="Raw content of the note")
    type: NoteType = Field(..., description="Type of content (text, link, file, audio, image)")
    user_id: str = Field(..., description="Identifier of the user who created the note")
    timestamp: int = Field(default_factory=lambda: int(time.time()), description="Unix timestamp of creation time")
    title: str = Field(..., description="Title of the note")

    class Config:
        json_schema_extra = {
            "example": {
                "id": "n7m6l5k4-j3i2-h1g0-f9e8-d7c6b5a4321",
                "input": "The cell is the basic structural, functional, and biological unit of all known organisms.",
                "type": "text",
                "user_id": "u1s2e3r4-5a6b7-8c9d0",
                "timestamp": 1698765432,
                "title": "Introduction to Cell Biology"
            }
        }

class Quiz(BaseModel):
    id: str = Field(..., description="Unique identifier for the quiz question")
    questions: str = Field(..., description="The quiz question text")
    choices: List[str] = Field(..., description="List of possible answers", min_items=2)
    answer: str = Field(..., description="The correct answer from the choices list")
    note_id: str = Field(..., description="Reference to the parent note")

    class Config:
        json_schema_extra = {
            "example": {
                "id": "q9w8e7r6-t5y4-u3i2-o1p0-a2s3d4f5g6h7",
                "questions": "Which organelle is responsible for protein synthesis in cells?",
                "choices": ["Mitochondria", "Ribosome", "Golgi apparatus", "Nucleus"],
                "answer": "Ribosome",
                "note_id": "n7m6l5k4-j3i2-h1g0-f9e8-d7c6b5a4321"
            }
        }
        
class Summary(BaseModel):
    id: str = Field(..., description="Unique identifier for the summary")
    content: str = Field(..., description="The summarized text content")
    note_id: str = Field(..., description="Reference to the parent note")

    class Config:
        json_schema_extra = {
            "example": {
                "id": "s1u2m3m4-a5r6y7-8i9d0-1a2b3c4d5e6f",
                "content": "Cells are the fundamental units of life containing various organelles like nucleus, mitochondria, and ribosomes that perform different functions crucial for survival.",
                "note_id": "n7m6l5k4-j3i2-h1g0-f9e8-d7c6b5a4321"
            }
        }

class TextCreate(BaseModel):
    text: str = Field(..., description="Raw text content to create a note from")

    class Config:
        json_schema_extra = {
            "example": {
                "text": "The cell is the basic structural, functional, and biological unit of all known organisms."
            }
        }

class LinkCreate(BaseModel):
    link: HttpUrl = Field(..., description="Web URL to extract content from")

    class Config:
        json_schema_extra = {
            "example": {
                "link": "https://en.wikipedia.org/wiki/Cell_biology"
            }
        }

class FileResponse(BaseModel):
    filename: str = Field(..., description="Name of the uploaded file")
    content_type: str = Field(..., description="MIME type of the file")
    note_id: str = Field(..., description="ID of the created note")

class ApiErrorResponse(BaseModel):
    detail: str = Field(..., description="Error message describing what went wrong")
    
    class Config:
        json_schema_extra = {
            "example": {
                "detail": "Note not found with the specified ID"
            }
        }