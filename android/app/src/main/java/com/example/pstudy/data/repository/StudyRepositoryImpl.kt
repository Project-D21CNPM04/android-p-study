package com.example.pstudy.data.repository

import com.example.pstudy.data.local.source.LocalDataSource
import com.example.pstudy.data.model.Content
import com.example.pstudy.data.model.FlashCard
import com.example.pstudy.data.model.MindMap
import com.example.pstudy.data.model.Quiz
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.data.model.Summary
import com.example.pstudy.data.remote.dto.SummaryDto
import com.example.pstudy.data.remote.source.RemoteDataSource
import com.example.pstudy.data.remote.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : StudyRepository {

    // Study materials operations
    override suspend fun getStudyMaterials(): Flow<List<StudyMaterials>> {
        return localDataSource.getAllStudyMaterials()
    }

    override suspend fun getStudyMaterial(id: String): StudyMaterials? {
        return localDataSource.getStudyMaterialById(id)
    }

    override suspend fun insertStudyMaterial(studyMaterial: StudyMaterials): String {
        return localDataSource.insertStudyMaterial(studyMaterial)
    }

    override suspend fun updateStudyMaterial(studyMaterial: StudyMaterials) {
        localDataSource.updateStudyMaterial(studyMaterial)
    }

    override suspend fun deleteStudyMaterial(id: String) {
        localDataSource.deleteStudyMaterial(id)
    }

    // FlashCard operations
    override suspend fun getFlashCards(noteId: String): List<FlashCard> {
        val localFlashCards = localDataSource.getFlashCardsByStudyMaterialId(noteId)
        if (localFlashCards.isNotEmpty()) {
            return localFlashCards
        }

        return when (val remoteResponse = remoteDataSource.getFlashCard(noteId)) {
            is NetworkResult.Success -> {
                val flashCardDto = remoteResponse.data
                val flashCard = FlashCard(
                    id = flashCardDto.id,
                    title = flashCardDto.title,
                    content = Content(
                        front = flashCardDto.content.front,
                        back = flashCardDto.content.back
                    )
                )
                localDataSource.insertFlashCards(listOf(flashCard), noteId)
                listOf(flashCard)
            }
            is NetworkResult.Error -> emptyList()
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun createFlashCards(flashCards: List<FlashCard>, studyMaterialId: String) {
        localDataSource.insertFlashCards(flashCards, studyMaterialId)
    }

    override suspend fun updateFlashCard(flashCard: FlashCard, studyMaterialId: String) {
        localDataSource.updateFlashCard(flashCard, studyMaterialId)
    }

    override suspend fun deleteFlashCard(id: Int) {
        localDataSource.deleteFlashCard(id)
    }

    override suspend fun generateFlashCards(noteId: String): NetworkResult<Unit> {
        return remoteDataSource.createFlashCard(noteId)
    }

    // Quiz operations
    override suspend fun getQuizzes(noteId: String): List<Quiz> {
        val localQuizzes = localDataSource.getQuizzesByStudyMaterialId(noteId)
        if (localQuizzes.isNotEmpty()) {
            return localQuizzes
        }

        return when (val remoteResponse = remoteDataSource.getQuiz(noteId)) {
            is NetworkResult.Success -> {
                val quizDto = remoteResponse.data
                val quiz = Quiz(
                    id = quizDto.id,
                    questions = quizDto.question,
                    choices = quizDto.choices,
                    answer = quizDto.answer
                )
                localDataSource.insertQuizzes(listOf(quiz), noteId)
                listOf(quiz)
            }
            is NetworkResult.Error -> emptyList()
            is NetworkResult.Loading -> emptyList()
        }
    }

    override suspend fun createQuizzes(quizzes: List<Quiz>, studyMaterialId: String) {
        localDataSource.insertQuizzes(quizzes, studyMaterialId)
    }

    override suspend fun updateQuiz(quiz: Quiz, studyMaterialId: String) {
        localDataSource.updateQuiz(quiz, studyMaterialId)
    }

    override suspend fun deleteQuiz(id: Int) {
        localDataSource.deleteQuiz(id)
    }

    override suspend fun generateQuiz(noteId: String): NetworkResult<Unit> {
        return remoteDataSource.createQuiz(noteId)
    }

    // MindMap operations
    override suspend fun getMindMap(noteId: String): MindMap? {
        val localMindMap = localDataSource.getMindMapByStudyMaterialId(noteId)
        if (localMindMap != null) {
            return localMindMap
        }

        return when (val remoteResponse = remoteDataSource.getMindMap(noteId)) {
            is NetworkResult.Success -> {
                val mindMapDto = remoteResponse.data
                val mindMap = MindMap(
                    id = mindMapDto.id,
                    content = mindMapDto.content,
                    summary = mindMapDto.summary
                )
                localDataSource.insertMindMap(mindMap, noteId)
                mindMap
            }
            is NetworkResult.Error -> null
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun createMindMap(mindMap: MindMap, studyMaterialId: String): String {
        return localDataSource.insertMindMap(mindMap, studyMaterialId)
    }

    override suspend fun updateMindMap(mindMap: MindMap, studyMaterialId: String) {
        localDataSource.updateMindMap(mindMap, studyMaterialId)
    }

    override suspend fun deleteMindMap(id: String) {
        localDataSource.deleteMindMap(id)
    }

    override suspend fun generateMindMap(noteId: String): NetworkResult<Unit> {
        return remoteDataSource.createMindMap(noteId)
    }

    // Summary operations
    override suspend fun getSummary(noteId: String): Summary? {
        val localSummary = localDataSource.getSummaryByStudyMaterialId(noteId)
        if (localSummary != null) {
            return localSummary
        }

        return when (val remoteResponse = remoteDataSource.getSummary(noteId)) {
            is NetworkResult.Success -> {
                val summaryDto = remoteResponse.data
                val summary = Summary(
                    id = summaryDto.id,
                    content = summaryDto.content,
                    noteId = noteId
                )
                localDataSource.insertSummary(summary)
                summary
            }
            is NetworkResult.Error -> null
            is NetworkResult.Loading -> null
        }
    }

    override suspend fun insertSummary(summary: Summary): String {
        return localDataSource.insertSummary(summary)
    }

    override suspend fun updateSummary(summary: Summary) {
        localDataSource.updateSummary(summary)
    }

    override suspend fun deleteSummary(id: String) {
        localDataSource.deleteSummary(id)
    }

    override suspend fun createTextNoteSummary(text: String): NetworkResult<SummaryDto> {
        return remoteDataSource.createTextNote(text)
    }

    override suspend fun createLinkNoteSummary(link: String): NetworkResult<SummaryDto> {
        return remoteDataSource.createLinkNote(link)
    }

    override suspend fun createFileNoteSummary(filePath: String): NetworkResult<SummaryDto> {
        val file = File(filePath)
        val requestBody = RequestBody.create(null, file)
        return remoteDataSource.createFileNote(requestBody)
    }
}