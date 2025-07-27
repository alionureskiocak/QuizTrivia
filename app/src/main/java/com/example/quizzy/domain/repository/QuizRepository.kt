package com.example.quizzy.domain.repository

import com.example.quizzy.domain.model.Quiz
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Difficulty

interface QuizRepository {

    suspend fun getQuestions(category : Category, difficulty: Difficulty?) : Quiz
}