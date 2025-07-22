package com.example.quizzy.presentation.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizzy.presentation.QuizViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(viewModel: QuizViewModel = hiltViewModel()) {
    val state by viewModel.state
    val answerList = state.answerList
    val currentQuestion = state.currentQuestion
    val selectedAnswer = state.selectedAnswer
    val correctAnswer = state.correctAnswer
    val fiftyJokerStayedList = state.fiftyJokerStayedList
    var fiftyJokerEnabled by remember { mutableStateOf(false) }
    val jokerCount = state.jokerCount
    val timeLeft = viewModel.timeLeft
    val visibleAnswers = answerList.filter {
        !fiftyJokerEnabled || fiftyJokerStayedList.contains(it)
    }

    LaunchedEffect(selectedAnswer) {
        if (selectedAnswer != null) {
            delay(2000)
            viewModel.getNewQuestion()
            fiftyJokerEnabled = false
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8) // Apple'vari soft arkaplan
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Progress + soru sayısı
            LinearProgressIndicator(
                progress = viewModel.questionCount / 10f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF4CAF50),
                trackColor = Color(0xFFE0E0E0)
            )

            Text(
                text = "Soru ${viewModel.questionCount} / 10",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Soru metni
            Text(
                text = currentQuestion.questionString,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 28.sp,
                color = Color(0xFF212121)
            )

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                visibleAnswers.forEach { answer ->
                    val isSelected = answer == selectedAnswer
                    val isCorrect = answer == correctAnswer

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                selectedAnswer == null -> Color.White
                                isCorrect -> Color(0xFFDFF5E1) // yeşilimsi doğru
                                isSelected && !isCorrect -> Color(0xFFFFEBEB) // kırmızımsı yanlış
                                else -> Color.White
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = selectedAnswer == null) {
                                viewModel.onAnswerSelected(answer)
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = answer,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF212121)
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))

                // 50/50 Buton
                Button(
                    onClick = {
                        viewModel.useFiftyJoker()
                        fiftyJokerEnabled = true
                    },
                    enabled = jokerCount>0 && fiftyJokerEnabled == false,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)

                ) {
                    Text("50/50 Joker ($jokerCount)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(64.dp))
                Text(text = timeLeft.value.toString(),fontSize = 48.sp, color = Color.Gray)
            }
        }
    }
}
