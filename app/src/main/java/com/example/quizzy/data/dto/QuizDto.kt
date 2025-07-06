package com.example.quizzy.data.dto

import com.google.gson.annotations.SerializedName

data class QuizDto(
    val response_code: Int,
    @SerializedName("results")
    val questionDtos: List<QuestionDto>
)