package com.example.pstudy.data.remote.source

import com.example.pstudy.data.remote.dto.*
import com.example.pstudy.data.remote.service.StudyService
import com.example.pstudy.data.remote.utils.NetworkResult
import com.example.pstudy.data.remote.utils.safeApiCall
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val studyService: StudyService
) : RemoteDataSource {

    // Study materials/notes operations
    override suspend fun getNoteList(): NetworkResult<List<NoteDto>> {
        return safeApiCall { studyService.getNoteList() }
    }

    override suspend fun getNoteDetail(noteId: String): NetworkResult<NoteDto> {
        return safeApiCall { studyService.getNoteDetail(noteId) }
    }

    // FlashCard operations
    override suspend fun getFlashCard(noteId: String): NetworkResult<FlashCardDto> {
        return safeApiCall { studyService.getFlashCard(noteId) }
    }

    override suspend fun createFlashCard(noteId: String): NetworkResult<Unit> {
        return safeApiCall {
        studyService.createFlashCard(noteId)
            Response.success(Unit)
        }
    }

    // Quiz operations
    override suspend fun getQuiz(noteId: String): NetworkResult<QuizDto> {
        return safeApiCall { studyService.getQuiz(noteId) }
    }

    override suspend fun createQuiz(noteId: String): NetworkResult<Unit> {
        return safeApiCall {
        studyService.createQuiz(noteId)
            Response.success(Unit)
        }
    }

    // MindMap operations
    override suspend fun getMindMap(noteId: String): NetworkResult<MindMapDto> {
        return safeApiCall { studyService.getMindMap(noteId) }
    }

    override suspend fun createMindMap(noteId: String): NetworkResult<Unit> {
        return safeApiCall {
        studyService.createMindMap(noteId)
            Response.success(Unit)
        }
    }

    // Summary operations
    override suspend fun getSummary(noteId: String): NetworkResult<SummaryDto> {
        return safeApiCall { studyService.getSummary(noteId) }
    }

    override suspend fun createTextNote(text: String): NetworkResult<SummaryDto> {
        return safeApiCall { studyService.createText(text) }
    }

    override suspend fun createLinkNote(link: String): NetworkResult<SummaryDto> {
        return safeApiCall { studyService.createLink(link) }
    }

    override suspend fun createFileNote(file: RequestBody): NetworkResult<SummaryDto> {
        return safeApiCall { studyService.createFile(file) }
    }
}