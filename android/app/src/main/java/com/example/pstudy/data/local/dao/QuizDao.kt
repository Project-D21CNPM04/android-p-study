package com.example.pstudy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pstudy.data.local.entity.QuizEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizzes(quizzes: List<QuizEntity>)

    @Update
    suspend fun updateQuiz(quiz: QuizEntity)

    @Delete
    suspend fun deleteQuiz(quiz: QuizEntity)

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizById(id: Int): QuizEntity?

    @Query("SELECT * FROM quizzes WHERE studyMaterialId = :studyMaterialId")
    suspend fun getQuizzesByStudyMaterialId(studyMaterialId: String): List<QuizEntity>

    @Query("DELETE FROM quizzes WHERE studyMaterialId = :studyMaterialId")
    suspend fun deleteQuizzesByStudyMaterialId(studyMaterialId: String)
}