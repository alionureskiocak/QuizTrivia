package com.alionur.quizzy.presentation.ui.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.*
import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty
import kotlinx.coroutines.delay
import com.alionur.quizzy.data.model.Screen
import com.alionur.quizzy.R
import com.alionur.quizzy.presentation.ui.main.ParticleBackground


@Composable
private fun getScreenSizeCategory(): ScreenSizeCategory {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp

    return when {
        screenWidthDp < 360 || screenHeightDp < 640 -> ScreenSizeCategory.SMALL  // 5" phones
        screenWidthDp < 420 || screenHeightDp < 750 -> ScreenSizeCategory.MEDIUM // 6" phones
        else -> ScreenSizeCategory.LARGE // 7"+ tablets
    }
}

private enum class ScreenSizeCategory {
    SMALL, MEDIUM, LARGE
}

@Composable
private fun getResponsiveDimensions(): ResponsiveDimensions {
    val screenCategory = getScreenSizeCategory()

    return when (screenCategory) {
        ScreenSizeCategory.SMALL -> ResponsiveDimensions(
            horizontalPadding = 12.dp,
            verticalPadding = 16.dp,
            cardSpacing = 8.dp,
            questionFontSize = 16.sp,
            answerFontSize = 14.sp,
            titleFontSize = 14.sp,
            valueFontSize = 16.sp,
            cardRadius = 16.dp,
            statCardHeight = 60.dp,
            jokerHeight = 48.dp,
            answerPadding = 12.dp,
            questionPadding = 16.dp
        )
        ScreenSizeCategory.MEDIUM -> ResponsiveDimensions(
            horizontalPadding = 16.dp,
            verticalPadding = 20.dp,
            cardSpacing = 12.dp,
            questionFontSize = 18.sp,
            answerFontSize = 15.sp,
            titleFontSize = 12.sp,
            valueFontSize = 18.sp,
            cardRadius = 20.dp,
            statCardHeight = 65.dp,
            jokerHeight = 52.dp,
            answerPadding = 16.dp,
            questionPadding = 20.dp
        )
        ScreenSizeCategory.LARGE -> ResponsiveDimensions(
            horizontalPadding = 24.dp,
            verticalPadding = 24.dp,
            cardSpacing = 16.dp,
            questionFontSize = 20.sp,
            answerFontSize = 16.sp,
            titleFontSize = 14.sp,
            valueFontSize = 20.sp,
            cardRadius = 24.dp,
            statCardHeight = 70.dp,
            jokerHeight = 56.dp,
            answerPadding = 20.dp,
            questionPadding = 24.dp
        )
    }
}

private data class ResponsiveDimensions(
    val horizontalPadding: Dp,
    val verticalPadding: Dp,
    val cardSpacing: Dp,
    val questionFontSize: androidx.compose.ui.unit.TextUnit,
    val answerFontSize: androidx.compose.ui.unit.TextUnit,
    val titleFontSize: androidx.compose.ui.unit.TextUnit,
    val valueFontSize: androidx.compose.ui.unit.TextUnit,
    val cardRadius: Dp,
    val statCardHeight: Dp,
    val jokerHeight: Dp,
    val answerPadding: Dp,
    val questionPadding: Dp
)

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
fun QuizScreen(navController : NavHostController,viewModel: QuizViewModel = hiltViewModel(), category: Category, difficulty: Difficulty?) {
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
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }
    var showDialog by remember { mutableStateOf(false) }
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var isGameFinished by remember { mutableStateOf(false) }
    val dimensions = getResponsiveDimensions()

    val visibleAnswers = answerList.filter {
        !fiftyJokerEnabled || fiftyJokerStayedList.contains(it)
    }

    LaunchedEffect(isFirstLaunch) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            viewModel.cleanStateForNewGame()
            viewModel.getQuestions(category, difficulty)
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
            ModernCustomDialog(
                score = correctQuestionCount,
                onDismiss = { showDialog = false },
                onRestart = { isFirstLaunch = true },
                onGoToMainMenu = {navController.navigate(Screen.Home.route)},
                dimensions = dimensions
            )
        }
    }

    LaunchedEffect(timeLeft) {
        if (timeLeft==0 && questionNumber == 10){
            isGameFinished = true
        }
    }

    if (isLoading) {
        ModernStartingScreen(dimensions = dimensions)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()

                .background(
                    if (isDarkTheme) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1B2C42),
                                Color(0xFF101C2F)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFE8EAF6),
                                Color(0xFFFFFFFF)
                            )
                        )
                    }
                )
        ) {
            ParticleBackground(number = 20)
            if (selectedAnswer == correctAnswer && selectedAnswer != null) {
                ConfettiAnimation(modifier = Modifier.matchParentSize())
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = dimensions.horizontalPadding)
                    .padding(bottom = dimensions.statCardHeight + dimensions.verticalPadding * 2),
                verticalArrangement = Arrangement.spacedBy(dimensions.cardSpacing / 2)
            ) {
                Spacer(modifier = Modifier.height(dimensions.verticalPadding))
                ModernProgressBar(
                    progress = ((questionNumber).coerceIn(0, 10)) / 10f,
                    questionNumber = questionNumber,
                    dimensions = dimensions
                )

                Spacer(modifier = Modifier.height(dimensions.cardSpacing * 1.5f))

                ModernQuestionCard(
                    question = currentQuestion.question,
                    dimensions = dimensions
                )

                Spacer(modifier = Modifier.height(dimensions.verticalPadding))

                Column(verticalArrangement = Arrangement.spacedBy(dimensions.cardSpacing)) {
                    visibleAnswers.forEachIndexed { index, answer ->
                        ModernAnswerCard(
                            answer = answer,
                            isSelected = answer == selectedAnswer,
                            isCorrect = answer == correctAnswer,
                            selectedAnswer = selectedAnswer,
                            timeLeft = timeLeft,
                            index = index,
                            dimensions = dimensions,
                            onAnswerClick = {
                                if (questionNumber == 10) isGameFinished = true
                                viewModel.onAnswerSelected(answer)
                                if (answer == correctAnswer) viewModel.rightAnswer()
                            }
                        )
                    }
                }
                ModernJokerButton(
                    jokerCount = jokerCount,
                    fiftyJokerEnabled = fiftyJokerEnabled,
                    timeLeft = timeLeft,
                    selectedAnswer = selectedAnswer,
                    dimensions = dimensions,
                    onJokerClick = {
                        viewModel.useFiftyJoker()
                        fiftyJokerEnabled = true
                    }
                )
                Spacer(modifier = Modifier.height(dimensions.cardSpacing))
            }

            ModernBottomStats(
                timeLeft = timeLeft,
                correctQuestionCount = correctQuestionCount,
                questionNumber = questionNumber,
                dimensions = dimensions,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = dimensions.horizontalPadding)
                    .padding(bottom = dimensions.verticalPadding)
                    .navigationBarsPadding()
            )
        }
    }
}


@Composable
private fun ModernProgressBar(
    progress: Float,
    questionNumber: Int,
    dimensions: ResponsiveDimensions
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "progressAnimation"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AnimatedContent(
            targetState = questionNumber,
            transitionSpec = {
                slideInVertically(initialOffsetY = { it }) + fadeIn() togetherWith
                        slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            },
            label = "questionNumber"
        ) { qNum ->
            Text(
                text = "Question $qNum of 10",
                style = MaterialTheme.typography.titleSmall,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.8f)
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(

                containerColor = if (isDarkTheme) Color(0xFF2C3E50) else MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF3498DB),
                                    Color(0xFF2980B9)
                                )
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun ModernQuestionCard(
    question: String,
    dimensions: ResponsiveDimensions
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    AnimatedContent(
        targetState = question,
        transitionSpec = {
            fadeIn(tween(400)) + slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) togetherWith fadeOut(tween(200)) + slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(200)
            )
        },
        label = "questionTransition"
    ) { questionText ->
        Card(
            shape = RoundedCornerShape(dimensions.cardRadius),
            colors = CardDefaults.cardColors(

                containerColor = if (isDarkTheme) {
                    Color(0xFF2C405A)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            border = if (!isDarkTheme) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            } else {
                null
            },
            elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 4.dp else 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp)
                    .padding(dimensions.questionPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = questionText,
                    fontSize = dimensions.questionFontSize,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = (dimensions.questionFontSize.value + 6).sp,
                    textAlign = TextAlign.Center,
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.9f)
                    else Color.Black.copy(alpha = 0.87f)
                )
            }
        }
    }
}

@Composable
private fun ModernAnswerCard(
    answer: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    selectedAnswer: String? = null,
    timeLeft: Int,
    index: Int,
    dimensions: ResponsiveDimensions,
    onAnswerClick: () -> Unit
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }


    val defaultContainerColor = if (isDarkTheme) Color(0xFF3A506E) else MaterialTheme.colorScheme.surface

    val bgColor by animateColorAsState(
        targetValue = when {
            selectedAnswer == null -> defaultContainerColor
            isCorrect -> Color(0xFF2ECC71)
            isSelected && !isCorrect -> Color(0xFFE74C3C)
            else -> if (isDarkTheme) Color(0xFF4A6581) else Color(0xFFF5F5F5)
        },
        animationSpec = tween(300),
        label = "answerCardColor"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            selectedAnswer == null -> if (isDarkTheme) Color.White.copy(alpha = 0.85f)
            else Color.Black.copy(alpha = 0.87f)
            isCorrect || (isSelected && !isCorrect) -> Color.White
            else -> if (isDarkTheme) Color.White.copy(alpha = 0.6f)
            else MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "answerContentColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "answerCardScale"
    )

    Card(
        shape = RoundedCornerShape(dimensions.cardRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = if (!isDarkTheme && selectedAnswer == null) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        } else {
            null
        },
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                enabled = selectedAnswer == null && timeLeft > 0
            ) { onAnswerClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensions.answerPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            selectedAnswer != null -> Color.White.copy(alpha = 0.2f)
                            isDarkTheme -> Color(0xFF5A7596)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ('A' + index).toString(),
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
            Text(
                text = answer,
                fontSize = dimensions.answerFontSize,
                fontWeight = FontWeight.Medium,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )

        }
    }
}


@Composable
private fun ModernJokerButton(
    jokerCount: Int,
    fiftyJokerEnabled: Boolean,
    timeLeft: Int,
    selectedAnswer: String?,
    dimensions: ResponsiveDimensions,
    onJokerClick: () -> Unit
) {
    val enabled = jokerCount > 0 && !fiftyJokerEnabled && timeLeft > 0 && selectedAnswer == null
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    val pulse by animateFloatAsState(
        targetValue = if (enabled) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jokerPulse"
    )

    val containerColor by animateColorAsState(
        targetValue = if (enabled) {

            if (isDarkTheme) Color(0xFF3498DB) else MaterialTheme.colorScheme.primary
        } else {

            if (isDarkTheme) Color(0xFF4A6581) else MaterialTheme.colorScheme.surfaceContainer
        },
        label = "jokerContainerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (enabled) {
            Color.White
        } else {
            if (isDarkTheme) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        },
        label = "jokerContentColor"
    )
    Spacer(Modifier.width(8.dp))
    Card(
        shape = RoundedCornerShape(dimensions.cardRadius),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 4.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(dimensions.jokerHeight)
            .scale(pulse)
            .clickable(enabled = enabled) { onJokerClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⚡ 50/50",
                fontSize = dimensions.answerFontSize,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (enabled) {
                            if (isDarkTheme) Color.White.copy(alpha = 0.2f) else Color.White.copy(
                                alpha = 0.25f
                            )
                        } else if (isDarkTheme) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "x$jokerCount",
                    fontSize = (dimensions.answerFontSize.value - 2).sp,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }
        }
    }
}


@Composable
private fun ModernBottomStats(
    timeLeft: Int,
    correctQuestionCount: Int,
    questionNumber: Int,
    dimensions: ResponsiveDimensions,
    modifier: Modifier = Modifier
) {
    val totalTimeForQuestion = 15f

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensions.cardSpacing)
    ) {
        ModernStatCard(
            title = "Time",
            value = "${timeLeft}s",
            isUrgent = timeLeft <= 5,
            dimensions = dimensions,
            modifier = Modifier.weight(1f),
            progress = timeLeft / totalTimeForQuestion
        )
        ModernStatCard(
            title = "Score",
            value = "$correctQuestionCount/${maxOf(1, questionNumber) - 1}",
            isUrgent = false,
            dimensions = dimensions,
            modifier = Modifier.weight(1f),
            progress = null
        )
    }
}

@Composable
private fun ModernStatCard(
    title: String,
    value: String,
    isUrgent: Boolean,
    dimensions: ResponsiveDimensions,
    modifier: Modifier = Modifier,
    progress: Float? = null
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    val scale by animateFloatAsState(
        targetValue = if (isUrgent) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        ),
        label = "urgentScale"
    )

    Card(
        shape = RoundedCornerShape(dimensions.cardRadius),
        colors = CardDefaults.cardColors(

            containerColor = if (isDarkTheme) {
                Color(0xFF2C405A)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (!isDarkTheme) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        } else {
            null
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .scale(scale)
            .height(dimensions.statCardHeight)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (progress != null) {
                val animatedProgress by animateFloatAsState(
                    targetValue = progress,
                    animationSpec = tween(1000),
                    label = "progress"
                )
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(dimensions.statCardHeight - 12.dp),
                    color = if (isUrgent) Color(0xFFE74C3C) else Color(0xFF3498DB), // Canlı kırmızı/mavi
                    strokeWidth = 2.5.dp
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.75f)
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
                AnimatedContent(
                    targetState = value,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "valueTransition"
                ) { animatedValue ->
                    Text(
                        text = animatedValue,
                        fontSize = dimensions.valueFontSize,
                        fontWeight = FontWeight.Bold,
                        color = if (isUrgent) Color(0xFFE74C3C) // Canlı kırmızı
                        else if (isDarkTheme) Color.White
                        else Color.Black.copy(alpha = 0.87f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernStartingScreen(
    dimensions: ResponsiveDimensions,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    Box(
        modifier = Modifier
            .fillMaxSize()

            .background(
                if (isDarkTheme) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1B2C42),
                            Color(0xFF101C2F),
                            Color(0xFF0C1726)
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF8F9FA),
                            Color(0xFFE9ECEF),
                            Color(0xFFDEE2E6)
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensions.verticalPadding)
        ) {
            val lottieRes = if (isDarkTheme) R.raw.black_loading else R.raw.white_loading
            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRes))
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = LottieConstants.IterateForever
            )

            val lottieSize = when (getScreenSizeCategory()) {
                ScreenSizeCategory.SMALL -> 150.dp
                ScreenSizeCategory.MEDIUM -> 180.dp
                ScreenSizeCategory.LARGE -> 200.dp
            }

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(lottieSize)
            )

            Text(
                text = "Loading Questions...",
                fontSize = dimensions.questionFontSize,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.85f)
                else Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ModernCustomDialog(
    onDismiss: () -> Unit,
    onRestart: () -> Unit,
    onGoToMainMenu : () -> Unit,
    score: Int,
    dimensions: ResponsiveDimensions
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    onRestart()
                    onDismiss()
                },
                shape = RoundedCornerShape(dimensions.cardRadius),
                colors = ButtonDefaults.buttonColors(
                    // DEĞİŞİKLİK: Diyalog butonları da mavi tonlarında
                    containerColor = Color(0xFF3498DB),
                    contentColor = Color.White
                ),
                modifier = Modifier.height(dimensions.jokerHeight)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Play Again", fontSize = dimensions.answerFontSize, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    onGoToMainMenu()
                    onDismiss() },
                shape = RoundedCornerShape(dimensions.cardRadius),

                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF3498DB)
                ),
                border = BorderStroke(1.dp, Color(0xFF3498DB)),
                modifier = Modifier.height(dimensions.jokerHeight)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Home, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Main Menu", fontSize = dimensions.answerFontSize, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        icon = {
            val iconSize = when (getScreenSizeCategory()) {
                ScreenSizeCategory.SMALL -> 48.dp
                ScreenSizeCategory.MEDIUM -> 56.dp
                ScreenSizeCategory.LARGE -> 64.dp
            }

            Box(
                modifier = Modifier
                    .size(iconSize)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3498DB),
                                Color(0xFF2980B9)
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

            }
        },
        title = {
            Text(
                "Game Completed!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = dimensions.questionFontSize
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Your Final Score:",
                    fontSize = dimensions.answerFontSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "$score / 10",
                    fontSize = dimensions.valueFontSize,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}