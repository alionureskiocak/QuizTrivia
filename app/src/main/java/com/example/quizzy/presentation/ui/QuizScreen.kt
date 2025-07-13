package com.example.quizzy.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.quizzy.presentation.QuizViewModel

@Composable
fun QuizScreen(viewModel: QuizViewModel = hiltViewModel()) {
    val state by viewModel.state
    val answerList = state.answerList
    val currentQuestion = state.currentQuestion
    val selectedAnswer = state.selectedAnswer
    val correctAnswer = state.correctAnswer

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Question ${viewModel.questionCount}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = currentQuestion.questionString,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                answerList.forEach { answer ->

                    val isSelected = answer == selectedAnswer
                    val isCorrect = answer == correctAnswer
                    val buttonColor = when {
                        selectedAnswer == null -> MaterialTheme.colorScheme.primary
                        isCorrect -> Color.Green
                        isSelected && !isCorrect -> Color.Red
                        isCorrect -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    }

                    Button(
                        onClick = {
                            if(selectedAnswer == null){
                                viewModel.onAnswerSelected(answer)
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                    ) {
                        Text(text = answer, fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {viewModel.getNewQuestion()}) {
                Text(text = "new question")
            }
        }

    }
}
