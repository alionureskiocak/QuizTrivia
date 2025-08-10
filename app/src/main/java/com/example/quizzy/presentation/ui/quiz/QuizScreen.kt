package com.example.quizzy.presentation.ui.quiz

import android.content.res.Resources
import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Difficulty
import kotlinx.coroutines.delay
import com.example.quizzy.R

@Composable
fun ConfettiAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.partyy))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuizScreen(viewModel: QuizViewModel = hiltViewModel(), category: Category, difficulty: Difficulty?) {
    val state by viewModel.state
    val answerList = state.answerList
    val isLoading = state.isLoading
    val currentQuestion = state.currentQuestion
    val selectedAnswer = state.selectedAnswer
    val correctAnswer = state.correctAnswer
    val questionNumber = state.currentQuestionCount
    val correctQuestionCount = state.correctQuestionCount
    val fiftyJokerStayedList = state.fiftyJokerStayedList
    var fiftyJokerEnabled by remember { mutableStateOf(false) }
    val jokerCount = state.jokerCount
    val timeLeft by viewModel.timeLeft.collectAsState()
    val startCounter by viewModel.startCounter.collectAsState()
    val isCounting by viewModel.isCounting.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    var showDialog by remember { mutableStateOf(false) }
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var isGameFinished by remember { mutableStateOf(false) }

    val visibleAnswers = answerList.filter {
        !fiftyJokerEnabled || fiftyJokerStayedList.contains(it)
    }

    LaunchedEffect(isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            viewModel.cleanStateForNewGame()
            viewModel.getQuestions(category, difficulty)
            //viewModel.startCounter()
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
        if (isGameFinished) {
            viewModel.stopTimer()
            delay(2000)
            isGameFinished = false
            showDialog = true
        }
    }

    if (showDialog) {
        AnimatedVisibility(
            visible = showDialog,
            enter = scaleIn(tween(500)) + fadeIn(tween(500)),
            exit = scaleOut(tween(300)) + fadeOut(tween(300))
        ) {
            CustomDialog(
                score = correctQuestionCount,
                onDismiss = { showDialog = false },
                onRestart = { isFirstLaunch = true }
            )
        }
    }

    if (isLoading) {
        StartingScreen()
    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            // Confetti animasyonu hep açık
            ConfettiAnimation(modifier = Modifier.matchParentSize())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val progressAnim by animateFloatAsState(
                    targetValue = questionNumber / 10f,
                    animationSpec = tween(500)
                )

                LinearProgressIndicator(
                    progress = { progressAnim },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = colorScheme.primary,
                    trackColor = colorScheme.surfaceVariant
                )

                AnimatedContent(
                    targetState = questionNumber,
                    transitionSpec = {
                        (slideInVertically(initialOffsetY = { it }) + fadeIn()) with
                                (slideOutVertically(targetOffsetY = { -it }) + fadeOut())
                    }
                ) { qNum ->
                    Text("Question $qNum / 10", fontSize = 14.sp, color = colorScheme.onBackground)
                }

                AnimatedContent(
                    targetState = currentQuestion.questionString,
                    transitionSpec = {
                        fadeIn(tween(300)) + slideInHorizontally(initialOffsetX = { it / 2 }) with
                                fadeOut(tween(200)) + slideOutHorizontally(targetOffsetX = { -it / 2 })
                    }
                ) { questionText ->
                    Text(
                        text = questionText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 28.sp,
                        color = colorScheme.onBackground
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    visibleAnswers.forEach { answer ->
                        val isSelected = answer == selectedAnswer
                        val isCorrect = answer == correctAnswer

                        val bgColor by animateColorAsState(
                            targetValue = when {
                                selectedAnswer == null -> colorScheme.surfaceVariant
                                isCorrect -> Color(0xFF7DE882)
                                isSelected && !isCorrect -> Color(0xFFFA8181)
                                else -> colorScheme.surfaceVariant
                            },
                            animationSpec = tween(400)
                        )

                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1f,
                            animationSpec = tween(300)
                        )

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                            colors = CardDefaults.cardColors(containerColor = bgColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                .clickable(enabled = selectedAnswer == null && timeLeft > 0) {
                                    if (questionNumber == 10) isGameFinished = true
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
                        enabled = jokerCount > 0 && !fiftyJokerEnabled && timeLeft > 0 && selectedAnswer == null,
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
                            Text("Time Left", fontSize = 14.sp, fontWeight = FontWeight.Medium)

                            val maxTime = 15f
                            val targetProgress = (maxTime - timeLeft) / maxTime
                            val animatedProgress by animateFloatAsState(
                                targetValue = targetProgress,
                                animationSpec = tween(1000, easing = LinearEasing)
                            )

                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth(0.3f)
                                    .height(6.dp),
                                color = colorScheme.surfaceVariant,
                                trackColor = colorScheme.primary
                            )

                            val scale by animateFloatAsState(
                                targetValue = if (timeLeft <= 3) 1.4f else 1f,
                                animationSpec = tween(400)
                            )

                            Text(
                                text = "$timeLeft s",
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Score", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            AnimatedContent(
                                targetState = correctQuestionCount,
                                transitionSpec = { fadeIn() with fadeOut() }
                            ) {
                                Text("$it/${questionNumber - 1}", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StartingScreen(modifier : Modifier = Modifier) {

    val isDarkTheme = isSystemInDarkTheme()
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        val lottieRes = if (isDarkTheme) R.raw.black_loading else R.raw.white_loading
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = LottieConstants.IterateForever
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier.fillMaxSize()
        )
    }


}

@Composable
fun CustomDialog(onDismiss: () -> Unit, onRestart: () -> Unit, score: Int) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                onRestart()
                onDismiss()
            }, shape = RoundedCornerShape(8.dp)) {
                Text("Start Again", fontSize = 20.sp)
            }
        },
        dismissButton = {
            Button(onClick = { }, shape = RoundedCornerShape(8.dp)) {
                Text("Main Menu", fontSize = 20.sp)
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("Game Finished!", style = MaterialTheme.typography.headlineSmall) },
        text = { Text("Score: $score\n", fontSize = 20.sp) },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}
