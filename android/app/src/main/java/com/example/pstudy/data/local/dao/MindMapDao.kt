package com.example.pstudy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pstudy.data.local.entity.MindMapEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MindMapDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMindMap(mindMap: MindMapEntity): Long

    @Update
    suspend fun updateMindMap(mindMap: MindMapEntity)

    @Delete
    suspend fun deleteMindMap(mindMap: MindMapEntity)

    @Query("SELECT * FROM mind_maps WHERE id = :id")
    suspend fun getMindMapById(id: String): MindMapEntity?

    @Query("SELECT * FROM mind_maps WHERE studyMaterialId = :studyMaterialId")
    suspend fun getMindMapByStudyMaterialId(studyMaterialId: String): MindMapEntity?
}