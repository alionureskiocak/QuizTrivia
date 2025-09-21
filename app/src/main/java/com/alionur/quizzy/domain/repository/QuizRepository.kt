package com.alionur.quizzy.domain.repository

import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty
import com.alionur.quizzy.domain.model.Question

interface QuizRepository {

    suspend fun getQuestions(category : Category, difficulty: Difficulty?) : List<Question>
}