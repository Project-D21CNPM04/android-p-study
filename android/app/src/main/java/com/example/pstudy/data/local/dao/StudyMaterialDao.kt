package com.example.pstudy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pstudy.data.local.entity.StudyMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyMaterialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(studyMaterial: StudyMaterialEntity): Long

    @Update
    suspend fun updateStudyMaterial(studyMaterial: StudyMaterialEntity)

    @Delete
    suspend fun deleteStudyMaterial(studyMaterial: StudyMaterialEntity)

    @Query("SELECT * FROM study_materials WHERE id = :id")
    suspend fun getStudyMaterialById(id: String): StudyMaterialEntity?

    @Query("SELECT * FROM study_materials WHERE userId = :userId ORDER BY timeStamp DESC")
    fun getStudyMaterialsByUserId(userId: String): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials ORDER BY timeStamp DESC")
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>>
}