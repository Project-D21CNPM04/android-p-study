package com.example.pstudy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "flash_cards",
    foreignKeys = [
        ForeignKey(
            entity = StudyMaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["studyMaterialId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class FlashCardEntity(
    @PrimaryKey
    val id: String = "",
    val studyMaterialId: String,
    val title: String,
    val frontContent: String,
    val backContent: String
)