package com.example.pstudy.data.repository

import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.model.MindMap
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.Summary
import com.example.pstudy.data.remote.dto.FlashCardDto
import com.example.pstudy.data.remote.dto.MindMapDto
import com.example.pstudy.data.remote.dto.NoteDto
import com.example.pstudy.data.remote.dto.QuizDto
import com.example.pstudy.data.remote.dto.SummaryDto
import com.example.pstudy.data.remote.utils.NetworkResult
import kotlinx.coroutines.flow.Flow

interface StudyRepository {
    // Study materials operations
    suspend fun getStudyMaterials(): Flow<List<StudyMaterials>>
    suspend fun getStudyMaterial(id: String): StudyMaterials?
    suspend fun insertStudyMaterial(studyMaterial: StudyMaterials): String
    suspend fun updateStudyMaterial(studyMaterial: StudyMaterials)
    suspend fun deleteStudyMaterial(id: String)

    // Remote note operations
    suspend fun getRemoteNoteList(): NetworkResult<List<NoteDto>>
    suspend fun getRemoteNoteDetail(noteId: String): NetworkResult<NoteDto>

    // FlashCard operations
    suspend fun getFlashCards(noteId: String): List<FlashCard>
    suspend fun createFlashCards(flashCards: List<FlashCard>, studyMaterialId: String)
    suspend fun updateFlashCard(flashCard: FlashCard, studyMaterialId: String)
    suspend fun deleteFlashCard(id: Int)
    suspend fun generateFlashCards(
        noteId: String,
        numFlashCards: Int,
        difficulty: Int,
    ): NetworkResult<List<FlashCardDto>>

    // Quiz operations
    suspend fun getQuizzes(noteId: String): List<Quiz>
    suspend fun createQuizzes(quizzes: List<Quiz>, studyMaterialId: String)
    suspend fun updateQuiz(quiz: Quiz, studyMaterialId: String)
    suspend fun deleteQuiz(id: Int)
    suspend fun generateQuiz(
        noteId: String,
        numQuizzes: Int,
        difficulty: Int,
    ): NetworkResult<List<QuizDto>>

    // MindMap operations
    suspend fun getMindMap(noteId: String): MindMap?
    suspend fun createMindMap(mindMap: MindMap, studyMaterialId: String): String
    suspend fun updateMindMap(mindMap: MindMap, studyMaterialId: String)
    suspend fun deleteMindMap(id: String)
    suspend fun generateMindMap(
        noteId: String,
        numNodes: Int = 5,
        difficulty: Int = 2
    ): NetworkResult<MindMapDto>

    // Summary operations
    suspend fun getSummary(noteId: String): Summary?
    suspend fun insertSummary(summary: Summary): String
    suspend fun updateSummary(summary: Summary)
    suspend fun deleteSummary(id: String)
    suspend fun createTextNoteSummary(text: String): NetworkResult<SummaryDto>
    suspend fun createLinkNoteSummary(link: String): NetworkResult<SummaryDto>
    suspend fun createFileNoteSummary(fileUri: String): NetworkResult<SummaryDto>
    suspend fun createAudioNoteSummary(audioPath: String): NetworkResult<SummaryDto>
    suspend fun createImageNoteSummary(imageUri: String): NetworkResult<SummaryDto>
}