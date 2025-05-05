package com.example.pstudy.di

import com.example.pstudy.data.repository.StudyRepository
import com.example.pstudy.data.repository.StudyRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindStudyRepository(
        impl: StudyRepositoryImpl
    ): StudyRepository
}
