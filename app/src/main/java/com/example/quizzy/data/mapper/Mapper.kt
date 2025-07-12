package com.example.quizzy.data.mapper

import com.example.quizzy.data.dto.QuestionDto
import com.example.quizzy.data.dto.QuizDto
import com.example.quizzy.domain.model.Question
import com.example.quizzy.domain.model.Quiz

fun QuestionDto.toQuestion() : Question{
    return Question(
        category = category,
        correctAnswer = correct_answer,
        difficulty = difficulty,
        incorrectAnswers = incorrect_answers,
        questionString = questionString,
        type = type
    )
}

fun QuizDto.toQuiz() : Quiz{
    return Quiz(
        responseCode = response_code,
        questions = questionDtos
    )
}

// public final val category: String,
//    public final val correct_answer: String,
//    public final val difficulty: String,
//    public final val incorrect_answers: List<String>,
//    @field:SerializedName(value = "question")
//public final val questionString: String,
//    public final val type: String