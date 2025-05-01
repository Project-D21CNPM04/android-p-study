from fastapi import UploadFile, HTTPException
from .repository import Repository
from motor.motor_asyncio import AsyncIOMotorDatabase

class Service:
    def __init__(self):
        self.repo = Repository()

    async def get_quiz(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        quiz = await self.repo.get_quiz(db, id, user_id)
        if not quiz:
            raise HTTPException(status_code=404, detail="Quiz not found")
        return quiz

    async def get_summary(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        mindmap = await self.repo.get_mindmap(db, id, user_id)
        if not mindmap:
            raise HTTPException(status_code=404, detail="Mindmap not found")
        return mindmap

    async def get_mindmap(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        mindmap = await self.repo.get_mindmap(db, id, user_id)
        if not mindmap:
            raise HTTPException(status_code=404, detail="Mindmap not found")
        return mindmap

    async def get_flashcard(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        flashcard = await self.repo.get_flashcard(db, id, user_id)
        if not flashcard:
            raise HTTPException(status_code=404, detail="Flashcard not found")
        return flashcard

    async def get_node_list(self, db: AsyncIOMotorDatabase, user_id: str):
        nodes = await self.repo.get_node_list(db, user_id)
        return nodes

    async def get_node_detail(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        node = await self.repo.get_node_detail(db, id, user_id)
        if not node:
            raise HTTPException(status_code=404, detail="Node not found")
        return node

    async def create_text(self, db: AsyncIOMotorDatabase, text: str, user_id: str):
        return await self.repo.create_text(db, text, user_id)

    async def create_link(self, db: AsyncIOMotorDatabase, link: str, user_id: str):
        return await self.repo.create_link(db, link, user_id)

    async def create_file(self, db: AsyncIOMotorDatabase, file: UploadFile, user_id: str):
        return await self.repo.create_file(db, file.filename, user_id)