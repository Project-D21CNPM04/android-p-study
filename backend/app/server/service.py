from fastapi import UploadFile, HTTPException
from .repository import Repository
from motor.motor_asyncio import AsyncIOMotorDatabase


async def create_link(db: AsyncIOMotorDatabase, link: str, user_id: str):
    return None


async def create_file(db: AsyncIOMotorDatabase, file: UploadFile, user_id: str):
    return None


class Service:
    def __init__(self):
        self.repo = Repository()

    async def get_quiz(self, db: AsyncIOMotorDatabase, id: str):
        quiz = await self.repo.get_quiz(db, id)
        if not quiz:
            raise HTTPException(status_code=404, detail="Quiz not found")
        return quiz

    async def get_summary(self, db: AsyncIOMotorDatabase, id: str):
        mindmap = await self.repo.get_mindmap(db, id)
        if not mindmap:
            raise HTTPException(status_code=404, detail="Mindmap not found")
        return mindmap

    async def get_mindmap(self, db: AsyncIOMotorDatabase, id: str):
        mindmap = await self.repo.get_mindmap(db, id)
        if not mindmap:
            raise HTTPException(status_code=404, detail="Mindmap not found")
        return mindmap

    async def get_flashcard(self, db: AsyncIOMotorDatabase, id: str):
        flashcard = await self.repo.get_flashcard(db, id)
        if not flashcard:
            raise HTTPException(status_code=404, detail="Flashcard not found")
        return flashcard

    async def get_node_list(self, db: AsyncIOMotorDatabase, user_id: str):
        nodes = await self.repo.get_node_list(db, user_id)
        return nodes

    async def get_node_detail(self, db: AsyncIOMotorDatabase, id: str):
        note = await self.repo.get_node_detail(db, id)
        if not note:
            raise HTTPException(status_code=404, detail="Note not found")
        return note

    async def create_text(self, db: AsyncIOMotorDatabase, text: str, user_id: str):
        return await self.repo.create_text(db, text, user_id)

    async def create_link(self, db: AsyncIOMotorDatabase, link: str, user_id: str):
        print(link, user_id)
        return None

    async def create_file(self, db: AsyncIOMotorDatabase, file: UploadFile, user_id: str):
        print(file, user_id)
        return None