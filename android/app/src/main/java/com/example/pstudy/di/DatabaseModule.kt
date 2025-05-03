package com.example.pstudy.di

import android.content.Context
import androidx.room.Room
import com.example.pstudy.data.local.dao.*
import com.example.pstudy.data.local.db.PStudyDatabase
import com.example.pstudy.data.local.source.LocalDataSource
import com.example.pstudy.data.local.source.LocalDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePStudyDatabase(@ApplicationContext context: Context): PStudyDatabase {
        return Room.databaseBuilder(
            context,
            PStudyDatabase::class.java,
            "pstudy_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStudyMaterialDao(database: PStudyDatabase): StudyMaterialDao {
        return database.studyMaterialDao()
    }

    @Provides
    @Singleton
    fun provideFlashCardDao(database: PStudyDatabase): FlashCardDao {
        return database.flashCardDao()
    }

    @Provides
    @Singleton
    fun provideQuizDao(database: PStudyDatabase): QuizDao {
        return database.quizDao()
    }

    @Provides
    @Singleton
    fun provideMindMapDao(database: PStudyDatabase): MindMapDao {
        return database.mindMapDao()
    }

    @Provides
    @Singleton
    fun provideSummaryDao(database: PStudyDatabase): SummaryDao {
        return database.summaryDao()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(
        studyMaterialDao: StudyMaterialDao,
        flashCardDao: FlashCardDao,
        quizDao: QuizDao,
        mindMapDao: MindMapDao,
        summaryDao: SummaryDao
    ): LocalDataSource {
        return LocalDataSourceImpl(
            studyMaterialDao,
            flashCardDao,
            quizDao,
            mindMapDao,
            summaryDao
        )
    }
}