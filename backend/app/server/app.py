import os
from dotenv import load_dotenv
from fastapi import FastAPI, APIRouter, UploadFile, File, Depends
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase
from contextlib import asynccontextmanager
from .models import TextCreate, LinkCreate, Flashcard, Mindmap, Note, Quiz, Summary
from .service import Service
from fastapi.middleware.cors import CORSMiddleware
from urllib.parse import quote_plus
load_dotenv()

MONGODB_URL = os.getenv("MONGODB_URL", "mongodb://mongodb:27017")

USERNAME = os.getenv("MONGODB_USERNAME", "chiyeuemthoi2k33")
PASSWORD = os.getenv("MONGODB_PASSWORD", "Huongdz@2003")
CLUSTER = os.getenv("MONGODB_CLUSTER", "harry.ggquwfj.mongodb.net")
DB_NAME = os.getenv("DB_NAME", "study_app")

encoded_username = quote_plus(USERNAME)
encoded_password = quote_plus(PASSWORD)

MONGODB_URL = f"mongodb+srv://{encoded_username}:{encoded_password}@{CLUSTER}/?retryWrites=true&w=majority&appName=Harry"
router = APIRouter()
service = Service()

async def get_db(app=Depends(lambda: app)):
    return app.database

@router.get("/quiz/{note_id}", response_model=list[Quiz])
async def get_quiz(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_quiz(db, note_id)

@router.get("/summary/{note_id}", response_model=Summary)
async def get_summary(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_summary(db, note_id)

@router.get("/mindmap/{note_id}", response_model=Mindmap)
async def get_mindmap(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_mindmap(db, note_id)

@router.get("/flashcard/{note_id}", response_model=list[Flashcard])
async def get_flashcard(note_id: str,  db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_flashcard(db, note_id)

@router.get("/note", response_model=list[Note])
async def get_note_list(user_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_note_list(db, user_id)

@router.get("/note/{note_id}", response_model=Note)
async def get_note_detail(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_note_detail(db, note_id)

@router.post("/mindmap/{note_id}", response_model=Mindmap)
async def create_mindmap(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_mindmap(db, note_id)

@router.post("/flashcard/{note_id}", response_model=list[Flashcard])
async def create_flashcard(note_id: str, num_flashcards: int = 5, difficulty: int = 4, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_flashcard(db, note_id, num_flashcards, difficulty)

@router.post("/quiz/{note_id}", response_model=list[Quiz])
async def create_quiz(note_id: str, num_quizzes: int = 5, difficulty: int = 4, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_quiz(db, note_id, num_quizzes, difficulty)

@router.post("/create/text", response_model=Summary)
async def create_text(data: TextCreate, user_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_text(db, data.text, user_id)

@router.post("/create/link", response_model=Summary)
async def create_link(data: LinkCreate, user_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_link(db, data.link, user_id)

@router.post("/create/file", response_model=Summary)
async def create_file(file: UploadFile = File(...), user_id: str = None, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_file(db, file, user_id)

@router.post("/create/audio", response_model=Summary)
async def create_audio(file: UploadFile = File(...), user_id: str = None, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_audio(db, file, user_id)

@router.post("/create/image", response_model=Summary)
async def create_image(file: UploadFile = File(...), user_id: str = None, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_image(db, file, user_id)

# FastAPI app setup
@asynccontextmanager
async def lifespan(app: FastAPI):
    app.mongodb_client = AsyncIOMotorClient(MONGODB_URL)
    app.database = app.mongodb_client[DB_NAME]
    yield
    app.mongodb_client.close()

app = FastAPI(lifespan=lifespan)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
app.include_router(router)