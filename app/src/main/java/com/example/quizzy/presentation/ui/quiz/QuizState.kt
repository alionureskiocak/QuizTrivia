package com.example.quizzy.presentation.ui.quiz

import com.example.quizzy.domain.model.Question

data class QuizState(
    val questions : List<Question> = emptyList(),
    val currentQuestion : Question = Question("","","",listOf(),"",""),
    var selectedAnswer : String? = null,
    val answerList : ArrayList<String> = arrayListOf(),
    val currentQuestionCount : Int = 0,
    val correctQuestionCount : Int = 0,
    val fiftyJokerStayedList : List<String> = arrayListOf(),
    var jokerCount : Int = 2,
    val correctAnswer : String = currentQuestion.correctAnswer,
    val isLoading : Boolean = false,
    val errorMsg : String = ""
)