package com.example.quizzy.domain.model

import com.example.quizzy.data.dto.QuestionDto

data class Quiz(
    val responseCode: Int,
    val questions: List<Question>
)