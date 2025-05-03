package com.example.pstudy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "mind_maps",
    foreignKeys = [
        ForeignKey(
            entity = StudyMaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["studyMaterialId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MindMapEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val summary: String,
    val studyMaterialId: String
)