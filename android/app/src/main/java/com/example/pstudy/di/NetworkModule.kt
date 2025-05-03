package com.example.pstudy.di

import com.example.pstudy.data.remote.NetworkConfig
import com.example.pstudy.data.remote.service.StudyService
import com.example.pstudy.data.remote.source.RemoteDataSource
import com.example.pstudy.data.remote.source.RemoteDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(NetworkConfig.TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.TIME_OUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideStudyService(retrofit: Retrofit): StudyService {
        return retrofit.create(StudyService::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(studyService: StudyService): RemoteDataSource {
        return RemoteDataSourceImpl(studyService)
    }
}