package com.example.quizzy.data.di

import com.example.quizzy.data.remote.QuizAPI
import com.example.quizzy.data.repository.QuizRepositoryImpl
import com.example.quizzy.domain.repository.QuizRepository
import com.example.quizzy.domain.use_case.GetQuizUseCase
import com.example.quizzy.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideQuizAPI() : QuizAPI{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuizAPI::class.java)
    }
    @Singleton
    @Provides
    fun provideQuizRepository(api : QuizAPI) : QuizRepository{
        return QuizRepositoryImpl(api)
    }
    @Singleton
    @Provides
    fun provideGetQuizUseCase(repository : QuizRepository) = GetQuizUseCase(repository)
}