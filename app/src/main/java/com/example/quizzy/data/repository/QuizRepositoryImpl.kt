package com.example.quizzy.data.repository

import com.example.quizzy.data.mapper.toQuiz
import com.example.quizzy.data.remote.QuizAPI
import com.example.quizzy.domain.model.Quiz
import com.example.quizzy.domain.repository.QuizRepository
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Difficulty
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class QuizRepositoryImpl @Inject constructor(
    private val api : QuizAPI
) : QuizRepository{
    override suspend fun getQuestions(category : Category, difficulty: Difficulty?): Quiz {
        return api.getQuestions(category = category.category, difficulty = difficulty?.diff).toQuiz()
    }

}
