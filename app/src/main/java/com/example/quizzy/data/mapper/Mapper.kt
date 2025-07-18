package com.example.quizzy.data.mapper

import com.example.quizzy.data.dto.QuestionDto
import com.example.quizzy.data.dto.QuizDto
import com.example.quizzy.domain.model.Question
import com.example.quizzy.domain.model.Quiz
import com.example.quizzy.util.Constants.decodeHtml
import kotlin.String

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
        questions = questionDtos.map { it.toQuestion() }
    )
}

fun Question.cleaned(): Question {
    return this.copy(
        correctAnswer = correctAnswer.decodeHtml(),
        incorrectAnswers = incorrectAnswers.map { it.decodeHtml() },
        questionString = questionString.decodeHtml()
    )
}

// public final val category: String,
//    public final val correct_answer: String,
//    public final val difficulty: String,
//    public final val incorrect_answers: List<String>,
//    @field:SerializedName(value = "question")
//public final val questionString: String,
//    public final val type: String