package com.example.quizzy.presentation.ui.quiz

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Difficulty
import kotlinx.coroutines.delay


@Composable
fun QuizScreen(viewModel: QuizViewModel = hiltViewModel(), category : Category, difficulty : Difficulty?) {
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
    val colorScheme = MaterialTheme.colorScheme
    var showDialog by remember{mutableStateOf(false)}
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var isGameFinished by remember{mutableStateOf(false)}

    LaunchedEffect(isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            viewModel.cleanStateForNewGame()
            viewModel.getQuestions(category,difficulty)
            viewModel.startCounter()
        }
    }

    LaunchedEffect(selectedAnswer) {
        if (selectedAnswer != null && !isGameFinished) {
            delay(2000)
            viewModel.getNewQuestion()
            fiftyJokerEnabled = false
        }
    }

    LaunchedEffect(isGameFinished) {
        if(isGameFinished){
            viewModel.stopTimer()
            delay(2000)
            isGameFinished = false
            showDialog = true
        }

    }

    if (showDialog){
        CustomDialog(
            score = correctQuestionCount,
            highScore = 10,
            onDismiss = {
            showDialog = false
        }, onRestart = {
            isFirstLaunch = true

        })
    }

    if (isCounting.value) {
        StartingScreen(startCounter.value)
    }
    else {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp),
            color = colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                LinearProgressIndicator(
                    progress = {questionNumber / 10f},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = colorScheme.primary,
                    trackColor = colorScheme.surfaceVariant
                )

                Text(
                    text = "Question $questionNumber / 10",
                    fontSize = 14.sp,
                    color = colorScheme.onBackground
                )

                Text(
                    text = currentQuestion.questionString,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 28.sp,
                    color = colorScheme.onBackground
                )

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    visibleAnswers.forEach { answer ->
                        val isSelected = answer == selectedAnswer
                        val isCorrect = answer == correctAnswer

                        val bgColor = when {
                            selectedAnswer == null -> colorScheme.surfaceVariant
                            isCorrect -> Color(0xFF7DE882)
                            isSelected && !isCorrect -> Color(0xFFFA8181)
                            else -> colorScheme.surfaceVariant
                        }

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = selectedAnswer == null && timeLeft.value > 0) {
                                    if (questionNumber == 2){
                                        isGameFinished = true
                                        //return@clickable
                                    }
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
                                    color = colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            viewModel.useFiftyJoker()
                            fiftyJokerEnabled = true
                        },
                        enabled = jokerCount > 0 && !fiftyJokerEnabled &&
                                timeLeft.value > 0 && selectedAnswer == null,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary,
                            disabledContainerColor = colorScheme.primary.copy(alpha = 0.5f),
                            disabledContentColor = colorScheme.onPrimary.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
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
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.Medium
                            )

                            val maxTime = 15f
                            val targetProgress = (maxTime - timeLeft.value) / maxTime
                            val animatedProgress by animateFloatAsState(
                                targetValue = targetProgress,
                                animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                            )

                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth(0.3f)
                                    .height(6.dp),
                                color = colorScheme.surfaceVariant,
                                trackColor = colorScheme.primary
                            )

                            Text(
                                text = "${timeLeft.value}s",
                                fontSize = 40.sp,
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Score",
                                fontSize = 14.sp,
                                color = colorScheme.onBackground,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "$correctQuestionCount/${questionNumber - 1}",
                                fontSize = 40.sp,
                                color = colorScheme.onBackground,
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
fun StartingScreen(startCounter: Int) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Starting in $startCounter...",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = colorScheme.primary
            )
        }
    }
}


@Composable
fun CustomDialog(
    onDismiss : () -> Unit,
    onRestart : () -> Unit,
    score : Int,
    highScore : Int
                 ) {
    AlertDialog( onDismissRequest = {
        onDismiss()
    },
        confirmButton= {
            Button(onClick = {
                onRestart()
                onDismiss()
            },
                shape = RoundedCornerShape(8.dp)) {
                Text(text = "Start Again",fontSize = 20.sp)
            }
        },
        dismissButton = {
            Button(onClick = {
                //ANA MENÜYE DÖN
            },
                shape = RoundedCornerShape(8.dp)) {
                Text(text = "Main Menu",fontSize = 20.sp)
            }
        },
        icon = {
            Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Warning",
            tint = MaterialTheme.colorScheme.primary
        )},
        title = { Text(text = "Game Finished!", style = MaterialTheme.typography.headlineSmall)},
        text = {Text(text = "Score : $score\nHigh Score : $highScore",fontSize = 20.sp)},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

