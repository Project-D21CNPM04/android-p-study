# API Specification for Android App

## **1. User Authentication**

### **1.1 User Login**
**Use Firebase**

---

## **2. Quiz & Flashcard Management**

### **2.1 Upload PDF to Generate Quiz/Flashcards**
**Endpoint:**
```
POST /api/quiz/generate
```
**Description:**
Uploads a PDF file, and the server generates a quiz/flashcard set.

**Headers:**
```
Content-Type: multipart/form-data
```

**Request Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| uid | string | Unique user identifier |
| file | file | PDF file uploaded from the app |
| num | integer | Number of questions/flashcard to generate |
| type | integer | 0 for quizz, 1 for flashcard |

**Request Example For Question:** (Multipart Form Data)
```
uid=123456789abcdef
file=quiz_document.pdf
num=10
type=0
```

**Response:**
```json
{
  "status": "success",
  "quiz": {
    "quiz_id": "quiz_001",
    "title": "History Quiz",
    "num": 10,
    "questions": [
      {
        "question": "What is the capital of France?",
        "options": ["Berlin", "Madrid", "Paris", "Rome"],
        "answer": "Paris"
      }
    ]
  }
}
```

**Request Example For Flashcard:** (Multipart Form Data)
```
uid=123456789abcdef
file=flashcard_document.pdf
num=10
type=1
```

**Response:**
```json
{
  "status": "success",
  "quiz": {
    "flashcard_id": "quiz_001",
    "title": "Anime Flashcard",
    "num": 10,
    "flashcards": [
      {
        "front": "Paris",
        "back": "The capital of France"
      }
    ]
  }
}
```

### **2.2 Retrieve User’s Created Quizzes**
**Endpoint:**
```
GET /api/quiz/list
```
**Description:**
Fetches all quizzes created by the user.

**Request Parameters:**
```
uid=123456789abcdef
```

**Response:**
```json
{
  "status": "success",
  "quizzes": [
    {
      "quiz_id": "quiz_001",
      "title": "History Quiz",
      "num_questions": 10,
      "created_at": "2025-03-07T12:00:00Z"
    }
  ]
}
```

### **2.3 Retrieve User’s Created Flashcards**
**Endpoint:**
```
GET /api/flashcard/list
```
**Description:**
Fetches all flashcards created by the user.

**Request Parameters:**
```
uid=123456789abcdef
```

**Response:**
```json
{
  "status": "success",
  "quizzes": [
    {
      "quiz_id": "quiz_001",
      "title": "History Quiz",
      "num_questions": 10,
      "created_at": "2025-03-07T12:00:00Z"
    }
  ]
}
```

---

## **3. Mindmap Generation**

### **3.1 Upload PDF to Generate Mindmap**
**Endpoint:**
```
POST /api/mindmap/generate
```
**Description:**
Uploads a PDF file, and the server generates a mindmap, returning a URL for embedding.

**Headers:**
```
Content-Type: multipart/form-data
```

**Request Parameters:**
| Parameter | Type | Description |
|-----------|------|-------------|
| uid | string | Unique user identifier |
| file | file | PDF file uploaded from the app |

**Request Example:** (Multipart Form Data)
```
uid=123456789abcdef
file=mindmap_document.pdf
```

**Response:**
```json
{
  "status": "success",
  "mindmap_url": "https://example.com/mindmap/123456"
}
```

---

## **4. Error Handling**

### **Error Response Format**
If an error occurs, the server will respond with the following format:

```json
{
  "status": "error",
  "message": "Invalid file format. Please upload a PDF."
}
```

---

## **5. Security Considerations**
- All requests must include a valid `uid`.
- File size limits should be enforced to prevent server overload.

---
