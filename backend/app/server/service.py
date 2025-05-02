from fastapi import UploadFile, HTTPException

from .models import Mindmap
from .repository import Repository
from motor.motor_asyncio import AsyncIOMotorDatabase
from utils.url_extractor import extract_text_from_url
from .models import Note, NoteType
import uuid


class Service:
    def __init__(self):
        self.repo = Repository()

    async def get_quiz(self, db: AsyncIOMotorDatabase, note_id: str):
        quiz = await self.repo.get_quiz(db, note_id)
        if not quiz:
            raise HTTPException(status_code=404, detail="Quiz not found")
        return quiz

    async def get_summary(self, db: AsyncIOMotorDatabase, note_id: str):
        summary = await self.repo.get_summary(db, note_id)
        if not summary:
            raise HTTPException(status_code=404, detail="Summary not found")
        return summary

    async def get_mindmap(self, db: AsyncIOMotorDatabase, note_id: str):
        mindmap = await self.repo.get_mindmap(db, note_id)
        if not mindmap:
            raise HTTPException(status_code=404, detail="Mindmap not found")
        return mindmap

    async def get_flashcard(self, db: AsyncIOMotorDatabase, note_id: str):
        flashcard = await self.repo.get_flashcard(db, note_id)
        if not flashcard:
            raise HTTPException(status_code=404, detail="Flashcard not found")
        return flashcard

    async def get_node_list(self, db: AsyncIOMotorDatabase, user_id: str):
        notes = await self.repo.get_node_list(db, user_id)
        return notes

    async def get_node_detail(self, db: AsyncIOMotorDatabase, id: str):
        note = await self.repo.get_node_detail(db, id)
        if not note:
            raise HTTPException(status_code=404, detail="Note not found")
        return note

    async def create_mindmap(self, db: AsyncIOMotorDatabase, note_id: str):
        #Flow get Note By note_id
        #Create MindMap base on Note use AI
        #Save to database
        #Return Mindmap
        raise HTTPException(status_code=500, detail="Failed to create mindmap")

    async def create_flashcard(self, db: AsyncIOMotorDatabase, note_id: str):
        # Flow get Note By note_id
        # Create List flashcard base on Note use AI
        # Save to database
        #Return list flashcard
        raise HTTPException(status_code=500, detail="Failed to create flashcard")

    async def create_quiz(self, db: AsyncIOMotorDatabase, note_id: str):
        # Flow get Note By note_id
        # Create List of Quiz base on Note use AI
        # Save to database
        # Return List Quiz
        raise HTTPException(status_code=500, detail="Failed to create quiz")

    async def create_text(self, db: AsyncIOMotorDatabase, text: str, user_id: str):
        # Create Note
        # Create Summary base on Note use AI
        # Save to database
        # Return Summary
        return None

    async def create_link(self, db: AsyncIOMotorDatabase, link: str, user_id: str):
        # Link to Text
        # Create Note
        # Create Summary base on Note use AI
        # Save to database
        # Return Summary
        try:
            extracted_text = extract_text_from_url(link)
            note = Note(
                id=str(uuid.uuid4()),
                input=extracted_text,
                type=NoteType.LINK,
                user_id=user_id
            )
            await db["nodes"].insert_one(note.dict())

            return note
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to extract text from link: {str(e)}")

    async def create_file(self, db: AsyncIOMotorDatabase, file: UploadFile, user_id: str):
        # File to Text
        # Create Note
        # Create Summary base on Note use AI
        # Save to database
        # Return Summary
        return None