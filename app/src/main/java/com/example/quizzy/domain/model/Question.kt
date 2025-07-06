package com.example.quizzy.domain.model

import com.google.gson.annotations.SerializedName

data class Question(
    val category: String,
    val correctAnswer: String,
    val difficulty: String,
    val incorrectAnswers: List<String>,
    val questionString: String,
    val type: String
)