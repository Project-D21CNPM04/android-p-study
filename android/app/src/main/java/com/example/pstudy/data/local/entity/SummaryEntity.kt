package com.example.pstudy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "summaries",
    foreignKeys = [
        ForeignKey(
            entity = StudyMaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["studyMaterialId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SummaryEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val studyMaterialId: String
)