package com.example.pstudy.data.local.source

import com.example.pstudy.data.local.dao.*
import com.example.pstudy.data.local.entity.FlashCardEntity
import com.example.pstudy.data.local.entity.MindMapEntity
import com.example.pstudy.data.local.entity.QuizEntity
import com.example.pstudy.data.local.entity.SummaryEntity
import com.example.pstudy.data.mapper.toDomain
import com.example.pstudy.data.mapper.toEntity
import com.example.pstudy.data.mapper.toEntityList
import com.example.pstudy.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalDataSourceImpl @Inject constructor(
    private val studyMaterialDao: StudyMaterialDao,
    private val flashCardDao: FlashCardDao,
    private val quizDao: QuizDao,
    private val mindMapDao: MindMapDao,
    private val summaryDao: SummaryDao
) : LocalDataSource {

    // StudyMaterials operations
    override suspend fun insertStudyMaterial(studyMaterial: StudyMaterials): String {
        val entity = studyMaterial.toEntity()
        studyMaterialDao.insertStudyMaterial(entity)

        // Insert related entities if they exist
        studyMaterial.summary?.let {
            insertSummary(it)
        }

        studyMaterial.mindMap?.let {
            insertMindMap(it, entity.id)
        }

        studyMaterial.flashCards?.let {
            insertFlashCards(it, entity.id)
        }

        studyMaterial.quizzes?.let {
            insertQuizzes(it, entity.id)
        }

        return entity.id
    }

    override suspend fun updateStudyMaterial(studyMaterial: StudyMaterials) {
        val entity = studyMaterial.toEntity()
        studyMaterialDao.updateStudyMaterial(entity)

        // Update related entities if they exist
        studyMaterial.summary?.let {
            updateSummary(it)
        }

        studyMaterial.mindMap?.let {
            updateMindMap(it, entity.id)
        }

        // For FlashCards and Quizzes, we would typically need more complex logic
        // to handle updates, additions, and removals from the collections
    }

    override suspend fun deleteStudyMaterial(id: String) {
        val studyMaterial = studyMaterialDao.getStudyMaterialById(id)
        studyMaterial?.let {
            // Delete related entities (will be handled by Room's foreign key cascade)
            studyMaterialDao.deleteStudyMaterial(it)
        }
    }

    override suspend fun getStudyMaterialById(id: String): StudyMaterials? {
        val entity = studyMaterialDao.getStudyMaterialById(id) ?: return null

        // Load related data
        val summary = getSummaryByStudyMaterialId(entity.id)
        val mindMap = getMindMapByStudyMaterialId(entity.id)
        val flashCards = getFlashCardsByStudyMaterialId(entity.id)
        val quizzes = getQuizzesByStudyMaterialId(entity.id)

        return entity.toDomain(summary, mindMap, flashCards, quizzes)
    }

    override fun getStudyMaterialsByUserId(userId: String): Flow<List<StudyMaterials>> {
        return studyMaterialDao.getStudyMaterialsByUserId(userId).map { entities ->
            entities.map { entity ->
                // For each entity, we need to load its related data
                val summary = summaryDao.getSummaryByStudyMaterialId(entity.id)?.let {
                    it.toDomain()
                }
                val mindMap = mindMapDao.getMindMapByStudyMaterialId(entity.id)?.let {
                    it.toDomain()
                }
                val flashCards = flashCardDao.getFlashCardsByStudyMaterialId(entity.id)
                    .map { it.toDomain() }
                val quizzes = quizDao.getQuizzesByStudyMaterialId(entity.id)
                    .map { it.toDomain() }

                entity.toDomain(summary, mindMap, flashCards, quizzes)
            }
        }
    }

    override fun getAllStudyMaterials(): Flow<List<StudyMaterials>> {
        return studyMaterialDao.getAllStudyMaterials().map { entities ->
            entities.map { entity ->
                // For each entity, we need to load its related data
                val summary = summaryDao.getSummaryByStudyMaterialId(entity.id)?.let {
                    it.toDomain()
                }
                val mindMap = mindMapDao.getMindMapByStudyMaterialId(entity.id)?.let {
                    it.toDomain()
                }
                val flashCards = flashCardDao.getFlashCardsByStudyMaterialId(entity.id)
                    .map { it.toDomain() }
                val quizzes = quizDao.getQuizzesByStudyMaterialId(entity.id)
                    .map { it.toDomain() }

                entity.toDomain(summary, mindMap, flashCards, quizzes)
            }
        }
    }

    // FlashCard operations
    override suspend fun insertFlashCards(flashCards: List<FlashCard>, studyMaterialId: String) {
        val entities = flashCards.toEntityList(studyMaterialId)
        flashCardDao.insertFlashCards(entities)
    }

    override suspend fun updateFlashCard(flashCard: FlashCard, studyMaterialId: String) {
        val entity = flashCard.toEntity(studyMaterialId)
        flashCardDao.updateFlashCard(entity)
    }

    override suspend fun deleteFlashCard(id: Int) {
        val flashCard = flashCardDao.getFlashCardById(id)
        flashCard?.let {
            flashCardDao.deleteFlashCard(it)
        }
    }

    override suspend fun getFlashCardsByStudyMaterialId(studyMaterialId: String): List<FlashCard> {
        val entities = flashCardDao.getFlashCardsByStudyMaterialId(studyMaterialId)
        return entities.map { flashCardEntity ->
            FlashCardMapper.toDomain(flashCardEntity)
        }
    }

    // Quiz operations
    override suspend fun insertQuizzes(quizzes: List<Quiz>, studyMaterialId: String) {
        val entities = quizzes.toEntityList(studyMaterialId)
        quizDao.insertQuizzes(entities)
    }

    override suspend fun updateQuiz(quiz: Quiz, studyMaterialId: String) {
        val entity = quiz.toEntity(studyMaterialId)
        quizDao.updateQuiz(entity)
    }

    override suspend fun deleteQuiz(id: Int) {
        val quiz = quizDao.getQuizById(id)
        quiz?.let {
            quizDao.deleteQuiz(it)
        }
    }

    override suspend fun getQuizzesByStudyMaterialId(studyMaterialId: String): List<Quiz> {
        val entities = quizDao.getQuizzesByStudyMaterialId(studyMaterialId)
        return entities.map { quizEntity ->
            QuizMapper.toDomain(quizEntity)
        }
    }

    // MindMap operations
    override suspend fun insertMindMap(mindMap: MindMap, studyMaterialId: String): String {
        val entity = mindMap.toEntity(studyMaterialId)
        mindMapDao.insertMindMap(entity)
        return entity.id
    }

    override suspend fun updateMindMap(mindMap: MindMap, studyMaterialId: String) {
        val entity = mindMap.toEntity(studyMaterialId)
        mindMapDao.updateMindMap(entity)
    }

    override suspend fun deleteMindMap(id: String) {
        val mindMap = mindMapDao.getMindMapById(id)
        mindMap?.let {
            mindMapDao.deleteMindMap(it)
        }
    }

    override suspend fun getMindMapByStudyMaterialId(studyMaterialId: String): MindMap? {
        val entity = mindMapDao.getMindMapByStudyMaterialId(studyMaterialId)
        return entity?.let { MindMapMapper.toDomain(it) }
    }

    // Summary operations
    override suspend fun insertSummary(summary: Summary): String {
        val entity = summary.toEntity()
        summaryDao.insertSummary(entity)
        return entity.id
    }

    override suspend fun updateSummary(summary: Summary) {
        val entity = summary.toEntity()
        summaryDao.updateSummary(entity)
    }

    override suspend fun deleteSummary(id: String) {
        val summary = summaryDao.getSummaryById(id)
        summary?.let {
            summaryDao.deleteSummary(it)
        }
    }

    override suspend fun getSummaryByStudyMaterialId(studyMaterialId: String): Summary? {
        val entity = summaryDao.getSummaryByStudyMaterialId(studyMaterialId)
        return entity?.let { SummaryMapper.toDomain(it) }
    }
}

// Extension object classes for using static-like access to mapper functions
object FlashCardMapper {
    fun toDomain(entity: FlashCardEntity): FlashCard {
        return entity.toDomain()
    }
}

object QuizMapper {
    fun toDomain(entity: QuizEntity): Quiz {
        return entity.toDomain()
    }
}

object MindMapMapper {
    fun toDomain(entity: MindMapEntity): MindMap {
        return entity.toDomain()
    }
}

object SummaryMapper {
    fun toDomain(entity: SummaryEntity): Summary {
        return entity.toDomain()
    }
}