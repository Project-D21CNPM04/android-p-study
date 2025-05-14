package com.example.pstudy.data.local.source

import com.example.pstudy.data.model.*
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    // StudyMaterials operations
    suspend fun insertStudyMaterial(studyMaterial: StudyMaterials): String
    suspend fun updateStudyMaterial(studyMaterial: StudyMaterials)
    suspend fun deleteStudyMaterial(id: String)
    suspend fun getStudyMaterialById(id: String): StudyMaterials?
    fun getStudyMaterialsByUserId(userId: String): Flow<List<StudyMaterials>>
    fun getAllStudyMaterials(): Flow<List<StudyMaterials>>

    // FlashCard operations
    suspend fun insertFlashCards(flashCards: List<FlashCard>, studyMaterialId: String)
    suspend fun updateFlashCard(flashCard: FlashCard, studyMaterialId: String)
    suspend fun deleteFlashCard(id: Int)
    suspend fun getFlashCardsByStudyMaterialId(studyMaterialId: String): List<FlashCard>

    // Quiz operations
    suspend fun insertQuizzes(quizzes: List<Quiz>, studyMaterialId: String)
    suspend fun updateQuiz(quiz: Quiz, studyMaterialId: String)
    suspend fun deleteQuiz(id: Int)
    suspend fun getQuizzesByStudyMaterialId(studyMaterialId: String): List<Quiz>

    // MindMap operations
    suspend fun insertMindMap(mindMap: MindMap, studyMaterialId: String): String
    suspend fun updateMindMap(mindMap: MindMap, studyMaterialId: String)
    suspend fun deleteMindMap(id: String)
    suspend fun getMindMapByStudyMaterialId(studyMaterialId: String): MindMap?

    // Summary operations
    suspend fun insertSummary(summary: Summary): String
    suspend fun updateSummary(summary: Summary)
    suspend fun deleteSummary(id: String)
    suspend fun getSummaryByStudyMaterialId(studyMaterialId: String): Summary?
}