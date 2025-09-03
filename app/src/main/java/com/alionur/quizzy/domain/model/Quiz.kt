package com.alionur.quizzy.domain.model

data class Quiz(
    val responseCode: Int,
    val questions: List<Question>
)