package com.example.pstudy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.pstudy.data.local.entity.FlashCardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashCardDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashCard(flashCard: FlashCardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashCards(flashCards: List<FlashCardEntity>)

    @Update
    suspend fun updateFlashCard(flashCard: FlashCardEntity)

    @Delete
    suspend fun deleteFlashCard(flashCard: FlashCardEntity)

    @Query("SELECT * FROM flash_cards WHERE id = :id")
    suspend fun getFlashCardById(id: Int): FlashCardEntity?

    @Query("SELECT * FROM flash_cards WHERE studyMaterialId = :studyMaterialId")
    suspend fun getFlashCardsByStudyMaterialId(studyMaterialId: String): List<FlashCardEntity>

    @Query("DELETE FROM flash_cards WHERE studyMaterialId = :studyMaterialId")
    suspend fun deleteFlashCardsByStudyMaterialId(studyMaterialId: String)
}