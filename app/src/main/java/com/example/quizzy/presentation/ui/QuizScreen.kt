package com.example.quizzy.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.quizzy.presentation.QuizViewModel

@Composable
fun QuizScreen(viewModel : QuizViewModel = hiltViewModel()) {

    val state by viewModel.state
    val questions = state.questions


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
        ) {

        val currentQuestion = state.currentQuestion
        val answerList = state.answerList
        Text(text = currentQuestion.questionString,fontSize = 20.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
            ) {
            // 1ST BUTTON
            Button(onClick = {
                if (viewModel.isAnswerTrue(answerList[0])){
                    //correct answer
                }else{
                    //incorrect answer
                }
            }) {
                Text(text = answerList[0])
            }
            // 2ND BUTTON
            Button(onClick = {
                if (viewModel.isAnswerTrue(answerList[1])){
                    //correct answer
                }else{
                    //incorrect answer
                }
            }) {
                Text(text = answerList[1])
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 3ST BUTTON
            Button(onClick = {
                if (viewModel.isAnswerTrue(answerList[2])){
                    //correct answer
                }else{
                    //incorrect answer
                }
            }) {
                Text(text = answerList[2])
            }
            // 4ND BUTTON
            Button(onClick = {
                if (viewModel.isAnswerTrue(answerList[3])){
                    //correct answer
                }else{
                    //incorrect answer
                }
            }) {
                Text(text = answerList[3])
            }
        }
    }


}