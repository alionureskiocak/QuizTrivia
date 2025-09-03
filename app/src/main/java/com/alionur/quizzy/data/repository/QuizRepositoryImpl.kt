package com.alionur.quizzy.data.repository

import com.alionur.quizzy.data.mapper.toQuiz
import com.alionur.quizzy.data.remote.QuizAPI
import com.alionur.quizzy.domain.model.Quiz
import com.alionur.quizzy.domain.repository.QuizRepository
import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty
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
