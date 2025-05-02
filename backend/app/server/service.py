from fastapi import UploadFile, HTTPException
from ai_services.assistant import PromptAssistant
from .models import Mindmap, Flashcard, FlashcardContent, Quiz, Summary
from .repository import Repository
from motor.motor_asyncio import AsyncIOMotorDatabase
from utils.url_extractor import extract_text_from_url
from utils.mindmap_processing import text_to_mindmap
from .models import Note, NoteType
import uuid
import json


class Service:
    def __init__(self):
        self.repo = Repository()
        self.ai_assistant = PromptAssistant()

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

    async def get_note_list(self, db: AsyncIOMotorDatabase, user_id: str):
        notes = await self.repo.get_note_list(db, user_id)
        return notes

    async def get_note_detail(self, db: AsyncIOMotorDatabase, id: str):
        note = await self.repo.get_note_detail(db, id)
        if not note:
            raise HTTPException(status_code=404, detail="Note not found")
        return note

    async def create_mindmap(self, db: AsyncIOMotorDatabase, note_id: str):
        #Flow get Note By note_id
        #Create MindMap base on Note use AI
        #Save to database
        #Return Mindmap
        try:
            note = await self.repo.get_node_detail(db, note_id)
            if not note:
                raise HTTPException(status_code=404, detail="Note not found")
            
            mindmap_markdown = self.ai_assistant.summarize_text(note.input)
            mindmap_html = text_to_mindmap(mindmap_markdown)
            mindmap = Mindmap(
                id=str(uuid.uuid4()),
                content=mindmap_html,
                summary=note.input,
                note_id=note_id
            )
            saved_mindmap = await self.repo.create_mindmap(db, mindmap, note_id)
            return saved_mindmap
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to create mindmap: {str(e)}")

    async def create_flashcard(self, db: AsyncIOMotorDatabase, note_id: str):
        # Flow get Note By note_id
        # Create List flashcard base on Note use AI
        # Save to database
        #Return list flashcard
        try:
            note = await self.repo.get_node_detail(db, note_id)
            if not note:
                raise HTTPException(status_code=404, detail="Note not found")
            
            flashcards_json = self.ai_assistant.generate_flashcards(note.input)
            flashcards_data = json.loads(flashcards_json)
            flashcards_result = []
            
            for flashcard_item in flashcards_data:
                content = FlashcardContent(
                    front=flashcard_item["content"]["front"],
                    back=flashcard_item["content"]["back"]
                )     
                flashcard = Flashcard(
                    id=str(uuid.uuid4()),
                    title=flashcard_item["title"],
                    content=content,
                    note_id=note_id
                )
                saved_flashcard = await self.repo.create_flashcard(db, flashcard, note_id)
                flashcards_result.append(saved_flashcard)
            
            return flashcards_result
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to create flashcard: {str(e)}")

    async def create_quiz(self, db: AsyncIOMotorDatabase, note_id: str):
        # Flow get Note By note_id
        # Create List of Quiz base on Note use AI
        # Save to database
        # Return List Quiz
        try:
            note = await self.repo.get_node_detail(db, note_id)
            if not note:
                raise HTTPException(status_code=404, detail="Note not found")
            
            quizzes_json = self.ai_assistant.generate_quiz(note.input)
            quizzes_data = json.loads(quizzes_json)
            quizzes_result = []
            
            for quiz_item in quizzes_data:
                quiz = Quiz(
                    id=str(uuid.uuid4()),
                    questions=quiz_item["question"],
                    choices=quiz_item["choices"],
                    answer=quiz_item["answer"],
                    note_id=note_id
                )
                
                saved_quiz = await self.repo.create_quiz(db, quiz, note_id)
                quizzes_result.append(saved_quiz)
            
            return quizzes_result
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to create quiz: {str(e)}")

    async def create_text(self, db: AsyncIOMotorDatabase, text: str, user_id: str):
        # Create Note
        # Create Summary base on Note use AI
        # Save to database
        # Return Summary
        try:
            note = await self.repo.create_note(db, text, user_id)
            summary_content = self.ai_assistant.summarize_text(text)
            summary = await self.repo.create_summary(db, summary_content, note.id)
            return summary
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to process text: {str(e)}")


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
            await db["notes"].insert_one(note.dict())

            summary_content = self.ai_assistant.summarize_text(extracted_text)
            summary = await self.repo.create_summary(db, summary_content, note.id)
            return summary
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to extract text from link: {str(e)}")

    async def create_file(self, db: AsyncIOMotorDatabase, file: UploadFile, user_id: str):
        # File to Text
        # Create Note
        # Create Summary base on Note use AI
        # Save to database
        # Return Summary
        return None