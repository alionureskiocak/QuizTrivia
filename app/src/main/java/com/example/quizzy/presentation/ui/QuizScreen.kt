package com.example.quizzy.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quizzy.presentation.QuizViewModel

@Composable
fun QuizScreen(viewModel : QuizViewModel = hiltViewModel()) {

    val state = viewModel.state
    val questions = state.value.questions
    println(questions)

}