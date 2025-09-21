package com.alionur.quizzy.domain.model

data class Question(
    val category: String,
    val correctAnswer: String,
    val difficulty: String,
    val incorrectAnswers: List<String>,
    val question: String,
)