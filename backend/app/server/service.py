from fastapi import UploadFile, HTTPException
from ai_services.assistant import PromptAssistant
from .models import Mindmap, Flashcard, FlashcardContent, Quiz, Summary
from .repository import Repository
from motor.motor_asyncio import AsyncIOMotorDatabase
from utils.url_extractor import extract_text_from_url
from utils.mindmap_processing import text_to_mindmap
from utils.document_extractor import DocumentExtractor
from utils.image_extractor import extract_base64_from_image
from .models import Note, NoteType
from ai_services.audio_assistant import AudioAssistant
import firebase_admin
from firebase_admin import credentials, auth
import uuid
import json
import os
import tempfile


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
            note = await self.repo.get_note_detail(db, note_id)
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

    async def create_flashcard(self, db: AsyncIOMotorDatabase, note_id: str, num_flashcards: int = 5, difficulty: int = 4):
        # Flow get Note By note_id
        # Create List flashcard base on Note use AI
        # Save to database
        #Return list flashcard
        try:
            note = await self.repo.get_note_detail(db, note_id)
            if not note:
                raise HTTPException(status_code=404, detail="Note not found")
            
            # Convert numeric difficulty to text format
            difficulty_text = "Mixed"
            if difficulty == 1:
                difficulty_text = "Easy"
            elif difficulty == 2:
                difficulty_text = "Medium"
            elif difficulty == 3:
                difficulty_text = "Hard"
            
            flashcards_json = self.ai_assistant.generate_flashcards(note.input, num_flashcards, difficulty_text)
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

    async def create_quiz(self, db: AsyncIOMotorDatabase, note_id: str, num_quizzes: int = 5, difficulty: int = 4):
        # Flow get Note By note_id
        # Create List of Quiz base on Note use AI
        # Save to database
        # Return List Quiz
        try:
            note = await self.repo.get_note_detail(db, note_id)
            if not note:
                raise HTTPException(status_code=404, detail="Note not found")
            
            # Convert numeric difficulty to text format
            difficulty_text = "Mixed"
            if difficulty == 1:
                difficulty_text = "Easy"
            elif difficulty == 2:
                difficulty_text = "Medium"
            elif difficulty == 3:
                difficulty_text = "Hard"
            
            quizzes_json = self.ai_assistant.generate_quiz(note.input, num_quizzes, difficulty_text)
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
            title = self.ai_assistant.generate_title(text)
            note = await self.repo.create_note(db, text, user_id, title)
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
            title = self.ai_assistant.generate_title(extracted_text)
            note = Note(
                id=str(uuid.uuid4()),
                input=extracted_text,
                type=NoteType.LINK,
                user_id=user_id,
                title=title
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
        try:
            temp_dir = tempfile.gettempdir()
            file_path = os.path.join(temp_dir, file.filename)   
            with open(file_path, "wb") as temp_file:
                contents = await file.read()
                temp_file.write(contents)
            extractor = DocumentExtractor()
            extracted_text = extractor.extract_text_from_document(file_path)
            os.remove(file_path)
            if extracted_text.startswith("Error") or extracted_text.startswith("Unsupported"):
                raise HTTPException(status_code=400, detail=extracted_text)
            
            title = self.ai_assistant.generate_title(extracted_text)
            note = Note(
                id=str(uuid.uuid4()),
                input=extracted_text,
                type=NoteType.FILE,
                user_id=user_id,
                title=title
            )
            await db["notes"].insert_one(note.dict())
            summary_content = self.ai_assistant.summarize_text(extracted_text)
            summary = await self.repo.create_summary(db, summary_content, note.id)
            return summary
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to process file: {str(e)}")

    async def create_audio(self, db: AsyncIOMotorDatabase, file: UploadFile, user_id: str):
        # Audio to Text
        # Create Note
        # Create Summary base on Note use AI
        # Save to database
        # Return Summary
        try:
            temp_dir = tempfile.gettempdir()
            file_path = os.path.join(temp_dir, file.filename)   
            with open(file_path, "wb") as temp_file:
                contents = await file.read()
                temp_file.write(contents)
                
            audio_assistant = AudioAssistant()
            transcribed_text = audio_assistant.transcribe_audio(file_path)
            os.remove(file_path)
            
            if transcribed_text.startswith("Error"):
                raise HTTPException(status_code=400, detail=transcribed_text)
            
            title = self.ai_assistant.generate_title(transcribed_text)
            note = await self.repo.create_audio_note(db, transcribed_text, user_id, title)
            
            summary_content = self.ai_assistant.summarize_text(transcribed_text)
            summary = await self.repo.create_summary(db, summary_content, note.id)
            return summary
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to process audio: {str(e)}")
    
    async def create_image(self, db: AsyncIOMotorDatabase, file: UploadFile, user_id: str):
        # Image to Text
        # Create Note
        # Create Summary base on Note use AI
        # Save to database
        # Return Summary
        try:
            contents = await file.read()
            
            file_ext = file.filename.lower().split('.')[-1]
            supported_formats = ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp']
            
            if file_ext not in supported_formats:
                raise HTTPException(
                    status_code=400, 
                    detail=f"Unsupported image format: {file_ext}. Supported formats are: {', '.join(supported_formats)}"
                )
            
            base64_image = extract_base64_from_image(contents)
            
            if base64_image.startswith("Error"):
                raise HTTPException(status_code=400, detail=base64_image)
            
            extracted_text = self.ai_assistant.extract_text_from_image(base64_image)
            
            if not extracted_text or extracted_text == "[unreadable]":
                extracted_text = "Cannot extract text from image."
            
            title = self.ai_assistant.generate_title(extracted_text)
            note = Note(
                id=str(uuid.uuid4()),
                input=extracted_text,
                type=NoteType.IMAGE,
                user_id=user_id,
                title=title
            )
            await db["notes"].insert_one(note.dict())
            
            summary_content = self.ai_assistant.summarize_text(extracted_text)
            summary = await self.repo.create_summary(db, summary_content, note.id)
            
            return summary
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Failed to process image: {str(e)}")

    async def get_dashboard_statistics(self, db: AsyncIOMotorDatabase):

        days = ["Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"]
        content_types = ["Link", "Text", "Audio", "Image", "File"]
        
        try:
            notes_collection = db.notes
            quizzes_collection = db.quizzes
            flashcards_collection = db.flashcards
            mindmaps_collection = db.mindmaps
            
            if not firebase_admin._apps:
                cred = credentials.Certificate(os.path.join(os.path.dirname(__file__),"p-study-firebase-adminsdk-fbsvc-b7e0610921.json"))
                firebase_admin.initialize_app(cred)
            
            total_notes = await notes_collection.count_documents({})
            
            notes_by_hour = []
            for hour in range(24):
                try:
                    hour_count = await notes_collection.count_documents({
                        "$expr": {
                            "$eq": [
                                {"$hour": {"$toDate": {"$multiply": ["$timestamp", 1000]}}},
                                hour
                            ]
                        }
                    })
                    notes_by_hour.append({"name": f"{hour}:00", "value": hour_count})
                except Exception as e:
                    print(f"Error getting hour counts: {e}")
                    notes_by_hour.append({"name": f"{hour}:00", "value": 0})
            
            notes_by_day = []
            for day_idx, day_name in enumerate(days):
                try:
                    mongo_day = (day_idx + 1) % 7 + 1
                    day_count = await notes_collection.count_documents({
                        "$expr": {
                            "$eq": [
                                {"$dayOfWeek": {"$toDate": {"$multiply": ["$timestamp", 1000]}}},
                                mongo_day
                            ]
                        }
                    })
                    notes_by_day.append({"name": day_name, "value": day_count})
                except Exception as e:
                    print(f"Error getting day counts: {e}")
                    notes_by_day.append({"name": day_name, "value": 0})
            
            notes_by_type = []
            for content_type in content_types:
                try:
                    type_count = await notes_collection.count_documents({"type": content_type.lower()})
                    notes_by_type.append({"name": content_type, "value": type_count})
                except Exception as e:
                    print(f"Error getting type counts: {e}")
                    notes_by_type.append({"name": content_type, "value": 0})
            
            total_quizzes = await quizzes_collection.count_documents({})
            quizzes_by_type = []
            
            for content_type in content_types:
                try:
                    pipeline = [
                        {
                            "$lookup": {
                                "from": "notes",
                                "localField": "note_id",
                                "foreignField": "id",
                                "as": "note"
                            }
                        },
                        {"$match": {"note.type": content_type.lower()}},
                        {"$count": "count"}
                    ]
                    result = await quizzes_collection.aggregate(pipeline).to_list(1)
                    count = result[0]["count"] if result else 0
                    quizzes_by_type.append({"name": content_type, "value": count})
                except Exception as e:
                    print(f"Error getting quiz type counts: {e}")
                    quizzes_by_type.append({"name": content_type, "value": 0})
            
            total_flashcards = await flashcards_collection.count_documents({})
            flashcards_by_type = []
            
            for content_type in content_types:
                try:
                    pipeline = [
                        {
                            "$lookup": {
                                "from": "notes",
                                "localField": "note_id",
                                "foreignField": "id",
                                "as": "note"
                            }
                        },
                        {"$match": {"note.type": content_type.lower()}},
                        {"$count": "count"}
                    ]
                    result = await flashcards_collection.aggregate(pipeline).to_list(1)
                    count = result[0]["count"] if result else 0
                    flashcards_by_type.append({"name": content_type, "value": count})
                except Exception as e:
                    print(f"Error getting flashcard type counts: {e}")
                    flashcards_by_type.append({"name": content_type, "value": 0})
            
            total_mindmaps = await mindmaps_collection.count_documents({})
            mindmaps_by_type = []
            
            for content_type in content_types:
                try:
                    pipeline = [
                        {
                            "$lookup": {
                                "from": "notes",
                                "localField": "note_id",
                                "foreignField": "id",
                                "as": "note"
                            }
                        },
                        {"$match": {"note.type": content_type.lower()}},
                        {"$count": "count"}
                    ]
                    result = await mindmaps_collection.aggregate(pipeline).to_list(1)
                    count = result[0]["count"] if result else 0
                    mindmaps_by_type.append({"name": content_type, "value": count})
                except Exception as e:
                    print(f"Error getting mindmap type counts: {e}")
                    mindmaps_by_type.append({"name": content_type, "value": 0})
            
            try:
                firebase_users = auth.list_users().iterate_all()
                users = []
                for user in firebase_users:
                    users.append({
                        "id": user.uid, 
                        "name": user.email
                    })
                
                total_users = len(users)
                
                notes_per_user = []
                max_notes = 0
                min_notes = float('inf')
                total_user_notes = 0
                
                user_notes_data = []
                for user in users:
                    try:
                        user_notes_count = await notes_collection.count_documents({"user_id": user["id"]})
                        user_notes_data.append({"name": user["name"], "notes": user_notes_count})
                        max_notes = max(max_notes, user_notes_count)
                        min_notes = min(min_notes, user_notes_count)
                        total_user_notes += user_notes_count
                    except Exception as e:
                        print(f"Error getting user notes: {e}")
                
                # Sort users by note count in descending order and take only top 10
                user_notes_data.sort(key=lambda x: x["notes"], reverse=True)
                notes_per_user = user_notes_data[:10]
                
                avg_notes = round(total_user_notes / total_users) if total_users > 0 else 0
                min_notes = min_notes if min_notes != float('inf') else 0
            except Exception as e:
                print(f"Error getting Firebase users: {e}")
                total_users = 0
                notes_per_user = []
                max_notes = 0
                min_notes = 0
                avg_notes = 0
            
            return {
                "notes": {
                    "total": total_notes,
                    "byHour": notes_by_hour,
                    "byDay": notes_by_day,
                    "byType": notes_by_type
                },
                "quiz": {
                    "total": total_quizzes,
                    "byType": quizzes_by_type
                },
                "flashcard": {
                    "total": total_flashcards,
                    "byType": flashcards_by_type
                },
                "mindmap": {
                    "total": total_mindmaps,
                    "byType": mindmaps_by_type
                },
                "users": {
                    "total": total_users,
                    "notesPerUser": notes_per_user,
                    "maxNotes": max_notes,
                    "minNotes": min_notes,
                    "avgNotes": avg_notes
                }
            }
        except Exception as e:
            print(f"Error generating dashboard statistics: {e}")
            raise HTTPException(status_code=500, detail=f"Error generating dashboard statistics: {str(e)}")