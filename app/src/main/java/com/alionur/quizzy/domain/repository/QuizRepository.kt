package com.alionur.quizzy.domain.repository

import com.alionur.quizzy.domain.model.Quiz
import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty

interface QuizRepository {

    suspend fun getQuestions(category : Category, difficulty: Difficulty?) : Quiz
}