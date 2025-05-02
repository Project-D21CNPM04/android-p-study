import uuid
from motor.motor_asyncio import AsyncIOMotorDatabase
from .models import Flashcard, Mindmap, Note, Quiz, NoteType

class Repository:
    async def get_quiz(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        quiz = await db["quizzes"].find_one({"id": id, "user_id": user_id})
        if quiz:
            return Quiz(**quiz)
        return None

    async def get_mindmap(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        mindmap = await db["mindmaps"].find_one({"id": id, "user_id": user_id})
        if mindmap:
            return Mindmap(**mindmap)
        return None

    async def get_flashcard(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        flashcard = await db["flashcards"].find_one({"id": id, "user_id": user_id})
        if flashcard:
            return Flashcard(**flashcard)
        return None

    async def get_node_list(self, db: AsyncIOMotorDatabase, user_id: str):
        cursor = db["nodes"].find({"user_id": user_id})
        return [Note(**note) async for note in cursor]

    async def get_node_detail(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        note = await db["nodes"].find_one({"id": id, "user_id": user_id})
        if note:
            return Note(**note)
        return None



    async def create_text(self, db: AsyncIOMotorDatabase, text: str, user_id: str):
        note = Note(
            id=str(uuid.uuid4()),
            input=text,
            type=NoteType.TEXT,
            user_id=user_id
        )
        await db["nodes"].insert_one(note.dict())
        return note

    async def create_quiz(self, db: AsyncIOMotorDatabase, quiz: Quiz, user_id: str):
        quiz.id = str(uuid.uuid4())
        quiz.user_id = user_id
        await db["quizzes"].insert_one(quiz.dict())
        return quiz

    async def create_mindmap(self, db: AsyncIOMotorDatabase, mindmap: Mindmap, user_id: str):
        mindmap.id = str(uuid.uuid4())
        mindmap.user_id = user_id
        await db["mindmaps"].insert_one(mindmap.dict())
        return mindmap

    async def create_flashcard(self, db: AsyncIOMotorDatabase, flashcard: Flashcard, user_id: str):
        flashcard.id = str(uuid.uuid4())
        flashcard.user_id = user_id
        await db["flashcards"].insert_one(flashcard.dict())
        return flashcard