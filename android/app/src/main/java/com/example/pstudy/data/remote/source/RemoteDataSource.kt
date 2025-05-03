package com.example.pstudy.data.remote.source

import com.example.pstudy.data.model.*
import com.example.pstudy.data.remote.dto.*
import okhttp3.MultipartBody
import retrofit2.Response

interface RemoteDataSource {
    // Study materials/notes operations
    suspend fun getNoteList(): Response<List<NoteDto>>
    suspend fun getNoteDetail(noteId: String): Response<NoteDto>

    // FlashCard operations
    suspend fun getFlashCard(noteId: String): Response<FlashCardDto>
    suspend fun createFlashCard(noteId: String): Response<Unit>

    // Quiz operations
    suspend fun getQuiz(noteId: String): Response<QuizDto>
    suspend fun createQuiz(noteId: String): Response<Unit>

    // MindMap operations
    suspend fun getMindMap(noteId: String): Response<MindMapDto>
    suspend fun createMindMap(noteId: String): Response<Unit>

    // Summary operations
    suspend fun getSummary(noteId: String): Response<SummaryDto>
    suspend fun createTextNote(text: String): Response<SummaryDto>
    suspend fun createLinkNote(link: String): Response<SummaryDto>
    suspend fun createFileNote(file: MultipartBody.Part): Response<SummaryDto>
}