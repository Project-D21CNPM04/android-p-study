package com.example.pstudy.data.remote.service

import com.example.pstudy.data.remote.dto.FlashCardDto
import com.example.pstudy.data.remote.dto.MindMapDto
import com.example.pstudy.data.remote.dto.NoteDto
import com.example.pstudy.data.remote.dto.QuizDto
import com.example.pstudy.data.remote.dto.SummaryDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface StudyService {
    @GET("/quiz/{note_id}")
    suspend fun getQuiz(@Path("note_id") noteId: String): Response<QuizDto>

    @POST("/quiz/{note_id}")
    suspend fun createQuiz(@Path("note_id") noteId: String): Response<Unit>

    @GET("/summary/{note_id}")
    suspend fun getSummary(@Path("note_id") noteId: String): Response<SummaryDto>

    @GET("/mindmap/{note_id}")
    suspend fun getMindMap(@Path("note_id") noteId: String): Response<MindMapDto>

    @POST("/mindmap/{note_id}")
    suspend fun createMindMap(@Path("note_id") noteId: String): Response<Unit>

    @GET("/flashcard/{note_id}")
    suspend fun getFlashCard(@Path("note_id") noteId: String): Response<FlashCardDto>

    @POST("/flashcard/{note_id}")
    suspend fun createFlashCard(@Path("note_id") noteId: String): Response<Unit>

    @GET("/note")
    suspend fun getNoteList(): Response<List<NoteDto>>

    @GET("/note/{note_id}")
    suspend fun getNoteDetail(@Path("note_id") noteId: String): Response<NoteDto>

    @POST("/create/text")
    suspend fun createText(@Body request: String): Response<SummaryDto>

    @POST("/create/link")
    suspend fun createLink(@Body request: String): Response<SummaryDto>

    @Multipart
    @POST("/create/file")
    suspend fun createFile(@Part file: MultipartBody.Part): Response<SummaryDto>
}