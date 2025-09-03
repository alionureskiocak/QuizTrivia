package com.alionur.quizzy.data.dto

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    val category: String,
    val correct_answer: String,
    val difficulty: String,
    val incorrect_answers: List<String>,
    @SerializedName("question")
    val questionString: String,
    val type: String
)