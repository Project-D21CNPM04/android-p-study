package com.example.pstudy.data.remote.service

import com.example.pstudy.data.remote.LinkRequest
import com.example.pstudy.data.remote.TextRequest
import com.example.pstudy.data.remote.dto.FlashCardDto
import com.example.pstudy.data.remote.dto.MindMapDto
import com.example.pstudy.data.remote.dto.NoteDto
import com.example.pstudy.data.remote.dto.QuizDto
import com.example.pstudy.data.remote.dto.SummaryDto
import okhttp3.RequestBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface StudyService {
    @GET("/quiz/{note_id}")
    suspend fun getQuizzes(@Path("note_id") noteId: String): Response<List<QuizDto>>

    @POST("/quiz/{note_id}")
    suspend fun createQuizzes(
        @Path("note_id") noteId: String,
        @Query("num_quizzes") numQuizzes: Int,
        @Query("difficulty") difficulty: Int
    ): Response<List<QuizDto>>

    @GET("/summary/{note_id}")
    suspend fun getSummary(@Path("note_id") noteId: String): Response<SummaryDto>

    @GET("/mindmap/{note_id}")
    suspend fun getMindMap(@Path("note_id") noteId: String): Response<MindMapDto>

    @POST("/mindmap/{note_id}")
    suspend fun createMindMap(
        @Path("note_id") noteId: String,
        @Query("num_nodes") numNodes: Int,
        @Query("difficulty") difficulty: Int
    ): Response<MindMapDto>

    @GET("/flashcard/{note_id}")
    suspend fun getFlashCards(@Path("note_id") noteId: String): Response<List<FlashCardDto>>

    @POST("/flashcard/{note_id}")
    suspend fun createFlashCards(
        @Path("note_id") noteId: String,
        @Query("num_flashcards") numFlashcards: Int,
        @Query("difficulty") difficulty: Int
    ): Response<List<FlashCardDto>>

    @GET("/note")
    suspend fun getNoteList(@Query("user_id") userId: String): Response<List<NoteDto>>

    @GET("/note/{note_id}")
    suspend fun getNoteDetail(@Path("note_id") noteId: String): Response<NoteDto>

    @POST("/create/text")
    suspend fun createText(@Body request: TextRequest, @Query("user_id") userId: String): Response<SummaryDto>

    @POST("/create/link")
    suspend fun createLink(@Body request: LinkRequest, @Query("user_id") userId: String): Response<SummaryDto>

    @Multipart
    @POST("/create/file")
    suspend fun createFile(
        @Part file: MultipartBody.Part,
        @Query("user_id") userId: String
    ): Response<SummaryDto>

    @Multipart
    @POST("/create/audio")
    suspend fun createAudio(
        @Part file: MultipartBody.Part,
        @Query("user_id") userId: String
    ): Response<SummaryDto>

    @Multipart
    @POST("/create/image")
    suspend fun createImage(
        @Part file: MultipartBody.Part,
        @Query("user_id") userId: String
    ) : Response<SummaryDto>
}