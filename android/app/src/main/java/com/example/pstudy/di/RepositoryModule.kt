package com.example.pstudy.di

import android.content.Context
import com.example.pstudy.data.repository.StudyRepository
import com.example.pstudy.data.repository.StudyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindStudyRepository(
        impl: StudyRepositoryImpl
    ): StudyRepository

    companion object {
        @Provides
        @Singleton
        fun provideContext(@ApplicationContext context: Context): Context {
            return context
        }
    }
}