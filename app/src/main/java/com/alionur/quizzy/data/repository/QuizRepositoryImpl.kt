package com.alionur.quizzy.data.repository

import com.alionur.quizzy.data.remote.QuizAPI
import com.alionur.quizzy.domain.repository.QuizRepository
import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty
import com.alionur.quizzy.domain.model.Question
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class QuizRepositoryImpl @Inject constructor(
    private val api : QuizAPI
) : QuizRepository{
    override suspend fun getQuestions(category : Category, difficulty: Difficulty?): List<Question> {
        return  api.getQuestions(categories = category.category, difficulty = difficulty?.diff!!)
    }

}
