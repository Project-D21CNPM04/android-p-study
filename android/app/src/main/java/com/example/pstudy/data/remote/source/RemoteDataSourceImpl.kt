package com.example.pstudy.data.remote.source

import com.example.pstudy.data.remote.dto.*
import com.example.pstudy.data.remote.service.StudyService
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val studyService: StudyService
) : RemoteDataSource {

    // Study materials/notes operations
    override suspend fun getNoteList(): Response<List<NoteDto>> {
        return studyService.getNoteList()
    }

    override suspend fun getNoteDetail(noteId: String): Response<NoteDto> {
        return studyService.getNoteDetail(noteId)
    }

    // FlashCard operations
    override suspend fun getFlashCard(noteId: String): Response<FlashCardDto> {
        return studyService.getFlashCard(noteId)
    }

    override suspend fun createFlashCard(noteId: String): Response<Unit> {
        return studyService.createFlashCard(noteId)
    }

    // Quiz operations
    override suspend fun getQuiz(noteId: String): Response<QuizDto> {
        return studyService.getQuiz(noteId)
    }

    override suspend fun createQuiz(noteId: String): Response<Unit> {
        return studyService.createQuiz(noteId)
    }

    // MindMap operations
    override suspend fun getMindMap(noteId: String): Response<MindMapDto> {
        return studyService.getMindMap(noteId)
    }

    override suspend fun createMindMap(noteId: String): Response<Unit> {
        return studyService.createMindMap(noteId)
    }

    // Summary operations
    override suspend fun getSummary(noteId: String): Response<SummaryDto> {
        return studyService.getSummary(noteId)
    }

    override suspend fun createTextNote(text: String): Response<SummaryDto> {
        return studyService.createText(text)
    }

    override suspend fun createLinkNote(link: String): Response<SummaryDto> {
        return studyService.createLink(link)
    }

    override suspend fun createFileNote(file: MultipartBody.Part): Response<SummaryDto> {
        return studyService.createFile(file)
    }
}