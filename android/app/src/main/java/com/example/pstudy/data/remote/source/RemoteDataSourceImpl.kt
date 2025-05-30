package com.example.pstudy.data.remote.source

import com.example.pstudy.data.firebase.FirebaseAuthHelper
import com.example.pstudy.data.remote.LinkRequest
import com.example.pstudy.data.remote.TextRequest
import com.example.pstudy.data.remote.dto.FlashCardDto
import com.example.pstudy.data.remote.dto.MindMapDto
import com.example.pstudy.data.remote.dto.NoteDto
import com.example.pstudy.data.remote.dto.QuizDto
import com.example.pstudy.data.remote.dto.SummaryDto
import com.example.pstudy.data.remote.service.StudyService
import com.example.pstudy.data.remote.utils.NetworkResult
import com.example.pstudy.data.remote.utils.safeApiCall
import okhttp3.MultipartBody
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val studyService: StudyService
) : RemoteDataSource {

    // Study materials/notes operations
    override suspend fun getNoteList(): NetworkResult<List<NoteDto>> {
        return FirebaseAuthHelper.getCurrentUserUid()?.let { uid ->
            safeApiCall { studyService.getNoteList(uid) }
        } ?: NetworkResult.Error("User not authenticated")
    }

    override suspend fun getNoteDetail(noteId: String): NetworkResult<NoteDto> {
        return safeApiCall { studyService.getNoteDetail(noteId) }
    }

    // FlashCard operations
    override suspend fun getFlashCards(noteId: String): NetworkResult<List<FlashCardDto>> {
        return safeApiCall { studyService.getFlashCards(noteId) }
    }

    override suspend fun createFlashCards(
        noteId: String,
        numFlashCards: Int,
        difficulty: Int,
    ): NetworkResult<List<FlashCardDto>> {
        return safeApiCall { studyService.createFlashCards(noteId, numFlashCards, difficulty) }
    }

    // Quiz operations
    override suspend fun getQuizzes(noteId: String): NetworkResult<List<QuizDto>> {
        return safeApiCall { studyService.getQuizzes(noteId) }
    }

    override suspend fun createQuizzes(
        noteId: String,
        numQuizzes: Int,
        difficulty: Int,
    ): NetworkResult<List<QuizDto>> {
        return safeApiCall { studyService.createQuizzes(noteId, numQuizzes, difficulty) }
    }

    // MindMap operations
    override suspend fun getMindMap(noteId: String): NetworkResult<MindMapDto> {
        return safeApiCall { studyService.getMindMap(noteId) }
    }

    override suspend fun createMindMap(
        noteId: String,
        numNodes: Int,
        difficulty: Int
    ): NetworkResult<MindMapDto> {
        return safeApiCall { studyService.createMindMap(noteId, numNodes, difficulty) }
    }

    // Summary operations
    override suspend fun getSummary(noteId: String): NetworkResult<SummaryDto> {
        return safeApiCall { studyService.getSummary(noteId) }
    }

    override suspend fun createTextNote(text: String): NetworkResult<SummaryDto> {
        return FirebaseAuthHelper.getCurrentUserUid()?.let { uid ->
            safeApiCall { studyService.createText(TextRequest(text), uid) }
        } ?: NetworkResult.Error("User not authenticated")
    }

    override suspend fun createLinkNote(link: String): NetworkResult<SummaryDto> {
        return FirebaseAuthHelper.getCurrentUserUid()?.let { uid ->
            safeApiCall { studyService.createLink(LinkRequest(link), uid) }
        } ?: NetworkResult.Error("User not authenticated")
    }

    override suspend fun createFileNote(file: MultipartBody.Part): NetworkResult<SummaryDto> {
        return FirebaseAuthHelper.getCurrentUserUid()?.let { uid ->
            safeApiCall { studyService.createFile(file, uid) }
        } ?: NetworkResult.Error("User not authenticated")
    }

    override suspend fun createAudioNote(audio: MultipartBody.Part): NetworkResult<SummaryDto> {
        return FirebaseAuthHelper.getCurrentUserUid()?.let { uid ->
            safeApiCall { studyService.createAudio(audio, uid) }
        } ?: NetworkResult.Error("User not authenticated")
    }

    override suspend fun createImageNote(image: MultipartBody.Part): NetworkResult<SummaryDto> {
        return FirebaseAuthHelper.getCurrentUserUid()?.let { uid ->
            safeApiCall { studyService.createImage(image, uid) }
        } ?: NetworkResult.Error("User not authenticated")
    }
}