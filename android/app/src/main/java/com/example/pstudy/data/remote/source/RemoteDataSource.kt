package com.example.pstudy.data.remote.source

import com.example.pstudy.data.remote.dto.FlashCardDto
import com.example.pstudy.data.remote.dto.MindMapDto
import com.example.pstudy.data.remote.dto.NoteDto
import com.example.pstudy.data.remote.dto.QuizDto
import com.example.pstudy.data.remote.dto.SummaryDto
import com.example.pstudy.data.remote.utils.NetworkResult
import okhttp3.MultipartBody

interface RemoteDataSource {
    // Study materials/notes operations
    suspend fun getNoteList(): NetworkResult<List<NoteDto>>
    suspend fun getNoteDetail(noteId: String): NetworkResult<NoteDto>

    // FlashCard operations
    suspend fun getFlashCards(noteId: String): NetworkResult<List<FlashCardDto>>
    suspend fun createFlashCards(noteId: String): NetworkResult<List<FlashCardDto>>

    // Quiz operations
    suspend fun getQuizzes(noteId: String): NetworkResult<List<QuizDto>>
    suspend fun createQuizzes(noteId: String): NetworkResult<List<QuizDto>>

    // MindMap operations
    suspend fun getMindMap(noteId: String): NetworkResult<MindMapDto>
    suspend fun createMindMap(noteId: String): NetworkResult<MindMapDto>

    // Summary operations
    suspend fun getSummary(noteId: String): NetworkResult<SummaryDto>
    suspend fun createTextNote(text: String): NetworkResult<SummaryDto>
    suspend fun createLinkNote(link: String): NetworkResult<SummaryDto>
    suspend fun createFileNote(file: MultipartBody.Part): NetworkResult<SummaryDto>
}