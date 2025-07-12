package com.example.quizzy.domain.repository

import com.example.quizzy.domain.model.Quiz

interface QuizRepository {

    suspend fun getQuestions() : Quiz
}