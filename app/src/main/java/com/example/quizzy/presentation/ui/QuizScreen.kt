package com.example.quizzy.presentation.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizzy.presentation.QuizViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun QuizScreen(viewModel: QuizViewModel = hiltViewModel(),difficulty : String) {
    val state by viewModel.state
    val answerList = state.answerList
    val currentQuestion = state.currentQuestion
    val selectedAnswer = state.selectedAnswer
    val correctAnswer = state.correctAnswer
    val questionNumber = state.currentQuestionCount
    val correctQuestionCount = state.correctQuestionCount
    val fiftyJokerStayedList = state.fiftyJokerStayedList
    var fiftyJokerEnabled by remember { mutableStateOf(false) }
    val jokerCount = state.jokerCount
    val timeLeft = viewModel.timeLeft
    val visibleAnswers = answerList.filter {
        !fiftyJokerEnabled || fiftyJokerStayedList.contains(it)
    }
    val startCounter = viewModel.startCounter
    val isCounting = viewModel.isCounting

    LaunchedEffect(isCounting) {
        if (isCounting.value == false){
            viewModel.getQuestions(difficulty)
            viewModel.startCounter()

        }
    }

    LaunchedEffect(selectedAnswer) {
        if (selectedAnswer != null) {
            delay(2000)
            viewModel.getNewQuestion()
            fiftyJokerEnabled = false
            println(difficulty)
        }
    }

    if (isCounting.value){
        StartingScreen(startCounter.value)
    }else{
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            color = Color(0xFFF8F8F8),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Progress + soru sayısı
                LinearProgressIndicator(
                    progress = questionNumber / 10f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = Color(0xFF4CAF50),
                    trackColor = Color(0xFFE0E0E0)
                )

                Text(
                    text = "Question ${questionNumber} / 10",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

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
                                    selectedAnswer == null -> Color(0xFFEBEFEA)
                                    isCorrect -> Color(0xFF7DE882) // yeşilimsi doğru
                                    isSelected && !isCorrect -> Color(0xFFFA8181) // kırmızımsı yanlış
                                    else -> Color(0xFFEBEFEA)
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = selectedAnswer == null && timeLeft.value > 0) {
                                    viewModel.onAnswerSelected(answer)
                                    if (answer == correctAnswer) viewModel.rightAnswer()
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
                        enabled = jokerCount>0 && fiftyJokerEnabled == false &&
                                timeLeft.value>0 && selectedAnswer == null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White,
                            disabledContentColor =  Color(0xFF7AB27C)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clickable(enabled = timeLeft.value > 0) {}
                    ) {
                        Text("50/50 Joker ($jokerCount)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(64.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Time Left",
                                fontSize = 14.sp,
                                color = Color(0xFF757575),
                                fontWeight = FontWeight.Medium
                            )
                            val timeLeft = viewModel.timeLeft

                            val maxTime = 15f
                            val targetProgress = (maxTime-timeLeft.value) / maxTime
                            val animatedProgress by animateFloatAsState(
                                targetValue = targetProgress,
                                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                            )

                            LinearProgressIndicator(
                                progress = {animatedProgress},
                                modifier = Modifier
                                    .fillMaxWidth(0.3f)
                                    .height(6.dp),
                                color = Color(0xFFE0E0E0),
                                trackColor = Color(0xFF4CAF50),
                                gapSize = 0.dp,
                                drawStopIndicator = {}
                            )

                            Text(
                                text = "${timeLeft.value}s",
                                fontSize = 40.sp,
                                color = Color(0xFF212121),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Score",
                                fontSize = 14.sp,
                                color = Color(0xFF757575),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$correctQuestionCount/${questionNumber-1}",
                                fontSize = 40.sp,
                                color = Color(0xFF212121),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }


                }
            }
        }
    }



}

@Composable
fun StartingScreen(startCounter : Int) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF8F8F8)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Starting in $startCounter...",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4CAF50)
            )
        }
    }
}
