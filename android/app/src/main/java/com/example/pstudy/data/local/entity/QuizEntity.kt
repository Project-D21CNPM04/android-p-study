package com.example.pstudy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pstudy.data.local.converter.StringListConverter

@Entity(
    tableName = "quizzes",
    foreignKeys = [
        ForeignKey(
            entity = StudyMaterialEntity::class,
            parentColumns = ["id"],
            childColumns = ["studyMaterialId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@TypeConverters(StringListConverter::class)
data class QuizEntity(
    @PrimaryKey
    val id: String = "",
    val questions: String,
    val choices: List<String>,
    val answer: String,
    val studyMaterialId: String
)