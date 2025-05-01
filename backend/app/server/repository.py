import uuid
from motor.motor_asyncio import AsyncIOMotorDatabase
from .models import Flashcard, Mindmap, Node, Quiz, NodeType

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
        return [Node(**node) async for node in cursor]

    async def get_node_detail(self, db: AsyncIOMotorDatabase, id: str, user_id: str):
        node = await db["nodes"].find_one({"id": id, "user_id": user_id})
        if node:
            return Node(**node)
        return None

    async def create_text(self, db: AsyncIOMotorDatabase, text: str, user_id: str):
        node = Node(
            id=str(uuid.uuid4()),
            input=text,
            type=NodeType.TEXT,
            user_id=user_id
        )
        await db["nodes"].insert_one(node.dict())
        return node

    async def create_link(self, db: AsyncIOMotorDatabase, link: str, user_id: str):
        node = Node(
            id=str(uuid.uuid4()),
            input=link,
            type=NodeType.LINK,
            user_id=user_id
        )
        await db["nodes"].insert_one(node.dict())
        return node

    async def create_file(self, db: AsyncIOMotorDatabase, filename: str, user_id: str):
        node = Node(
            id=str(uuid.uuid4()),
            input=filename,
            type=NodeType.FILE,
            user_id=user_id
        )
        await db["nodes"].insert_one(node.dict())
        return node