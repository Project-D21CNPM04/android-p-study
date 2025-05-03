package com.example.pstudy.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pstudy.data.local.converter.MaterialTypeConverter
import com.example.pstudy.data.local.converter.StringListConverter
import com.example.pstudy.data.local.dao.*
import com.example.pstudy.data.local.entity.*

@Database(
    entities = [
        StudyMaterialEntity::class,
        FlashCardEntity::class,
        QuizEntity::class,
        MindMapEntity::class,
        SummaryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    MaterialTypeConverter::class,
    StringListConverter::class
)
abstract class PStudyDatabase : RoomDatabase() {
    abstract fun studyMaterialDao(): StudyMaterialDao
    abstract fun flashCardDao(): FlashCardDao
    abstract fun quizDao(): QuizDao
    abstract fun mindMapDao(): MindMapDao
    abstract fun summaryDao(): SummaryDao
}