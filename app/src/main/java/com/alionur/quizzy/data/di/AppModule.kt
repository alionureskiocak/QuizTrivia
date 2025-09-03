package com.alionur.quizzy.data.di

import com.alionur.quizzy.data.remote.QuizAPI
import com.alionur.quizzy.data.repository.QuizRepositoryImpl
import com.alionur.quizzy.domain.repository.QuizRepository
import com.alionur.quizzy.domain.use_case.GetQuizUseCase
import com.alionur.quizzy.util.Constants.BASE_URL
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