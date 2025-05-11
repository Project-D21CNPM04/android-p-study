import os
from dotenv import load_dotenv
from fastapi import FastAPI, APIRouter, UploadFile, File, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from motor.motor_asyncio import AsyncIOMotorClient, AsyncIOMotorDatabase
from contextlib import asynccontextmanager
from .models import TextCreate, LinkCreate, Flashcard, Mindmap, Note, Quiz, Summary
from .service import Service
from typing import List

load_dotenv()
MONGODB_URL = os.getenv("MONGODB_URL", "mongodb://mongodb:27017")
DB_NAME = os.getenv("DB_NAME", "study_app")

service = Service()

async def get_db(app=Depends(lambda: app)):
    return app.database

# API Routers by functionality
note_router = APIRouter(prefix="/note", tags=["Notes"])
quiz_router = APIRouter(prefix="/quiz", tags=["Quizzes"])
summary_router = APIRouter(prefix="/summary", tags=["Summaries"])
mindmap_router = APIRouter(prefix="/mindmap", tags=["Mind Maps"])
flashcard_router = APIRouter(prefix="/flashcard", tags=["Flashcards"])
content_router = APIRouter(prefix="/create", tags=["Content Creation"])

# Notes Endpoints
@note_router.get("", response_model=List[Note])
async def get_notes(user_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_note_list(db, user_id)

@note_router.get("/{note_id}", response_model=Note)
async def get_note_detail(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_note_detail(db, note_id)

# Quizzes Endpoints
@quiz_router.get("/{note_id}", response_model=List[Quiz])
async def get_quiz(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_quiz(db, note_id)

@quiz_router.post("/{note_id}", response_model=List[Quiz], status_code=status.HTTP_201_CREATED)
async def create_quiz(
    note_id: str, 
    num_quizzes: int = 5, 
    difficulty: int = 4, 
    db: AsyncIOMotorDatabase = Depends(get_db)
):
    return await service.create_quiz(db, note_id, num_quizzes, difficulty)

# Summaries Endpoints
@summary_router.get("/{note_id}", response_model=Summary)
async def get_summary(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_summary(db, note_id)

# Mind Maps Endpoints
@mindmap_router.get("/{note_id}", response_model=Mindmap)
async def get_mindmap(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_mindmap(db, note_id)

@mindmap_router.post("/{note_id}", response_model=Mindmap, status_code=status.HTTP_201_CREATED)
async def create_mindmap(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_mindmap(db, note_id)

# Flashcards Endpoints
@flashcard_router.get("/{note_id}", response_model=List[Flashcard])
async def get_flashcard(note_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.get_flashcard(db, note_id)

@flashcard_router.post("/{note_id}", response_model=List[Flashcard], status_code=status.HTTP_201_CREATED)
async def create_flashcard(
    note_id: str, 
    num_flashcards: int = 5, 
    difficulty: int = 4, 
    db: AsyncIOMotorDatabase = Depends(get_db)
):
    return await service.create_flashcard(db, note_id, num_flashcards, difficulty)

# Content Creation Endpoints
@content_router.post("/text", response_model=Summary, status_code=status.HTTP_201_CREATED)
async def create_text(data: TextCreate, user_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_text(db, data.text, user_id)

@content_router.post("/link", response_model=Summary, status_code=status.HTTP_201_CREATED)
async def create_link(data: LinkCreate, user_id: str, db: AsyncIOMotorDatabase = Depends(get_db)):
    return await service.create_link(db, data.link, user_id)

@content_router.post("/file", response_model=Summary, status_code=status.HTTP_201_CREATED)
async def create_file(
    file: UploadFile = File(...), 
    user_id: str = None, 
    db: AsyncIOMotorDatabase = Depends(get_db)
):
    return await service.create_file(db, file, user_id)

@content_router.post("/audio", response_model=Summary, status_code=status.HTTP_201_CREATED)
async def create_audio(
    file: UploadFile = File(...), 
    user_id: str = None, 
    db: AsyncIOMotorDatabase = Depends(get_db)
):
    return await service.create_audio(db, file, user_id)

@content_router.post("/image", response_model=Summary, status_code=status.HTTP_201_CREATED)
async def create_image(
    file: UploadFile = File(...), 
    user_id: str = None, 
    db: AsyncIOMotorDatabase = Depends(get_db)
):
    return await service.create_image(db, file, user_id)

# FastAPI app setup
@asynccontextmanager
async def lifespan(app: FastAPI):
    app.mongodb_client = AsyncIOMotorClient(MONGODB_URL)
    app.database = app.mongodb_client[DB_NAME]
    yield
    app.mongodb_client.close()

app = FastAPI(
    title="P-Study API Documentation",
    description="API for creating, retrieving, and managing study materials like notes, quizzes, flashcards, and mind maps.",
    version="1.0.0",
    lifespan=lifespan
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include all routers
app.include_router(note_router)
app.include_router(quiz_router)
app.include_router(summary_router)
app.include_router(mindmap_router)
app.include_router(flashcard_router)
app.include_router(content_router)