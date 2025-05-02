import uuid
from motor.motor_asyncio import AsyncIOMotorDatabase
from .models import Flashcard, Mindmap, Note, Quiz, NoteType, Summary


class Repository:
    async def get_quiz(self, db: AsyncIOMotorDatabase, note_id: str):
        quizzes = await db["quizzes"].find({"note_id": note_id}).to_list(None)
        return [Quiz(**quiz) for quiz in quizzes] if quizzes else []

    async def get_mindmap(self, db: AsyncIOMotorDatabase, note_id: str):
        mindmap = await db["mindmaps"].find_one({"note_id": note_id})
        if mindmap:
            return Mindmap(**mindmap)
        return None

    async def get_flashcard(self, db: AsyncIOMotorDatabase, note_id: str):
        flashcards = await db["flashcards"].find({"note_id": note_id}).to_list(None)
        return [Flashcard(**flashcard) for flashcard in flashcards] if flashcards else []

    async def get_summary(self, db: AsyncIOMotorDatabase, note_id: str):
        summary = await db["summaries"].find_one({"note_id": note_id})
        if summary:
            return Summary(**summary)
        return None

    async def get_node_list(self, db: AsyncIOMotorDatabase, user_id: str):
        cursor = db["notes"].find({"user_id": user_id})
        return [Note(**note) async for note in cursor]

    async def get_node_detail(self, db: AsyncIOMotorDatabase, id: str):
        note = await db["notes"].find_one({"id": id})
        if note:
            return Note(**note)
        return None

    async def create_note(self, db: AsyncIOMotorDatabase, text: str, user_id: str):
        note = Note(
            id=str(uuid.uuid4()),
            input=text,
            type=NoteType.TEXT,
            user_id=user_id
        )
        await db["notes"].insert_one(note.dict())
        return note

    async def create_quiz(self, db: AsyncIOMotorDatabase, quiz: Quiz, note_id: str):
        quiz.id = str(uuid.uuid4())
        quiz.note_id = note_id
        await db["quizzes"].insert_one(quiz.dict())
        return quiz

    async def create_mindmap(self, db: AsyncIOMotorDatabase, mindmap: Mindmap, note_id: str):
        mindmap.id = str(uuid.uuid4())
        mindmap.note_id = note_id
        await db["mindmaps"].insert_one(mindmap.dict())
        return mindmap

    async def create_flashcard(self, db: AsyncIOMotorDatabase, flashcard: Flashcard, note_id: str):
        flashcard.id = str(uuid.uuid4())
        flashcard.note_id = note_id
        await db["flashcards"].insert_one(flashcard.dict())
        return flashcard

    async def create_summary(self, db: AsyncIOMotorDatabase, content: str, note_id: str):
        summary = Summary(
            id=str(uuid.uuid4()),
            content=content,
            note_id=note_id
        )
        await db["summaries"].insert_one(summary.dict())
        return summary