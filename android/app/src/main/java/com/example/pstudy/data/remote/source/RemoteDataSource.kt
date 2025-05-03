package com.example.pstudy.data.remote.source

import com.example.pstudy.data.remote.dto.FlashCardDto
import com.example.pstudy.data.remote.dto.MindMapDto
import com.example.pstudy.data.remote.dto.NoteDto
import com.example.pstudy.data.remote.dto.QuizDto
import com.example.pstudy.data.remote.dto.SummaryDto
import com.example.pstudy.data.remote.utils.NetworkResult
import okhttp3.RequestBody

interface RemoteDataSource {
    // Study materials/notes operations
    suspend fun getNoteList(): NetworkResult<List<NoteDto>>
    suspend fun getNoteDetail(noteId: String): NetworkResult<NoteDto>

    // FlashCard operations
    suspend fun getFlashCard(noteId: String): NetworkResult<FlashCardDto>
    suspend fun createFlashCard(noteId: String): NetworkResult<Unit>

    // Quiz operations
    suspend fun getQuiz(noteId: String): NetworkResult<QuizDto>
    suspend fun createQuiz(noteId: String): NetworkResult<Unit>

    // MindMap operations
    suspend fun getMindMap(noteId: String): NetworkResult<MindMapDto>
    suspend fun createMindMap(noteId: String): NetworkResult<Unit>

    // Summary operations
    suspend fun getSummary(noteId: String): NetworkResult<SummaryDto>
    suspend fun createTextNote(text: String): NetworkResult<SummaryDto>
    suspend fun createLinkNote(link: String): NetworkResult<SummaryDto>
    suspend fun createFileNote(file: RequestBody): NetworkResult<SummaryDto>
}