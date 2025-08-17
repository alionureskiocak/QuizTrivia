package com.example.quizzy.presentation.ui.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.*
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Difficulty
import kotlinx.coroutines.delay
import com.example.quizzy.R
import com.example.quizzy.presentation.ui.quiz.ResponsiveDimensions

// Responsive utility functions
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
            cardRadius = 12.dp,
            statCardHeight = 55.dp,
            jokerHeight = 45.dp,
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
            cardRadius = 16.dp,
            statCardHeight = 60.dp,
            jokerHeight = 50.dp,
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
            cardRadius = 20.dp,
            statCardHeight = 65.dp,
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
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }
    var showDialog by remember { mutableStateOf(false) }
    var isFirstLaunch by rememberSaveable { mutableStateOf(true) }
    var isGameFinished by remember { mutableStateOf(false) }
    println(currentQuestion.difficulty)
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
                                Color(0xFF0F0F23),
                                Color(0xFF1A1A2E),
                                Color(0xFF16213E)
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
                )
        ) {
            // Subtle confetti animation
            if (selectedAnswer == correctAnswer && selectedAnswer != null) {
                ConfettiAnimation(modifier = Modifier.matchParentSize())
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimensions.horizontalPadding)
                    .navigationBarsPadding() // <— alt çubuğa çakışmayı engelle
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(dimensions.cardSpacing)
            ) {
                // Top spacing
                Spacer(modifier = Modifier.height(dimensions.verticalPadding))

                // Modern Progress Bar — 0/10’dan başlat
                ModernProgressBar(
                    progress = ((questionNumber).coerceIn(0, 10)) / 10f,
                    questionNumber = questionNumber,
                    dimensions = dimensions
                )

                // Question Card
                ModernQuestionCard(
                    question = currentQuestion.questionString,
                    questionNumber = questionNumber,
                    dimensions = dimensions
                )

                // Answer Cards
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

                // Joker Button (güncellendi)
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

                // Push content up and stats to bottom
                Spacer(modifier = Modifier.weight(1f))

                // Stats at the bottom
                ModernBottomStats(
                    timeLeft = timeLeft,
                    correctQuestionCount = correctQuestionCount,
                    questionNumber = questionNumber,
                    dimensions = dimensions
                )

                // Bottom spacing
                Spacer(modifier = Modifier.height(dimensions.verticalPadding))
            }
        }
    }
}

@Composable
private fun ModernProgressBar(
    progress: Float,
    questionNumber: Int,
    dimensions: ResponsiveDimensions
) {
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
                fontSize = dimensions.titleFontSize,
                fontWeight = FontWeight.SemiBold,
                color = if (!MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f })
                    Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.7f)
            )
        }

        Card(
            shape = RoundedCornerShape(50.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
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
    questionNumber: Int,
    dimensions: ResponsiveDimensions
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    AnimatedContent(
        targetState = question,
        transitionSpec = {
            fadeIn(tween(400)) + slideInHorizontally(
                initialOffsetX = { it / 3 },
                animationSpec = tween(400, easing = FastOutSlowInEasing)
            ) togetherWith fadeOut(tween(200)) + slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(200)
            )
        },
        label = "questionTransition"
    ) { questionText ->
        Card(
            shape = RoundedCornerShape(dimensions.cardRadius),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme)
                    Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensions.questionPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = questionText,
                    fontSize = dimensions.questionFontSize,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = (dimensions.questionFontSize.value + 6).sp,
                    textAlign = TextAlign.Center,
                    color = if (isDarkTheme) Color.White else Color.Black
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

    // Card ve iç katmanlar için tek renk
    val bgColor by animateColorAsState(
        targetValue = when {
            selectedAnswer == null -> if (isDarkTheme) Color.White.copy(alpha = 0.08f) else Color.White.copy(alpha = 0.9f)
            isCorrect -> Color(0xFF4CAF50)
            isSelected && !isCorrect -> Color(0xFFE57373)
            else -> if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Gray.copy(alpha = 0.25f)
        },
        animationSpec = tween(300),
        label = "answerCardColor"
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
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(containerColor = bgColor),
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(bgColor) // İç katmanı Card ile eşleştirdik
                .padding(dimensions.answerPadding),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Option Circle
                val circleSize = when (getScreenSizeCategory()) {
                    ScreenSizeCategory.SMALL -> 24.dp
                    ScreenSizeCategory.MEDIUM -> 28.dp
                    ScreenSizeCategory.LARGE -> 32.dp
                }

                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .background(bgColor, CircleShape), // Circle da aynı renk
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ('A' + index).toString(),
                        fontSize = (dimensions.answerFontSize.value - 2).sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkTheme) Color.White else Color.Black
                    )
                }

                Text(
                    text = answer,
                    fontSize = dimensions.answerFontSize,
                    fontWeight = FontWeight.Medium,
                    color = when {
                        selectedAnswer == null -> if (isDarkTheme) Color.White else Color.Black
                        isCorrect -> Color.White
                        isSelected && !isCorrect -> Color.White
                        else -> if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
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

    // Hafif pulse animasyonu sadece aktifken
    val pulse by animateFloatAsState(
        targetValue = if (enabled) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "jokerPulse"
    )

    val enabledGradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        )
    )
    val disabledColor = if (isDarkTheme) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f)

    Card(
        shape = RoundedCornerShape(dimensions.cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (enabled) 8.dp else 2.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .scale(pulse)
            .clickable(enabled = enabled) { onJokerClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.jokerHeight)
                .clip(RoundedCornerShape(dimensions.cardRadius))
                .background(
                    if (enabled) enabledGradient
                    else Brush.linearGradient(listOf(disabledColor, disabledColor))
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Emoji kullandım ki ikon bağımlılığı sıkıntı çıkarmasın
                Text(
                    text = if (enabled) "⚡ 50/50" else "50/50",
                    fontSize = dimensions.answerFontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) Color.White else {
                        if (isDarkTheme) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f)
                    }
                )
                Spacer(Modifier.width(8.dp))
                // Counter pill
                Box(
                    modifier = Modifier
                        .height((dimensions.answerFontSize.value + 8).dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (enabled) Color.White.copy(alpha = 0.2f)
                            else Color.Black.copy(alpha = 0.08f)
                        )
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "x$jokerCount",
                        fontSize = (dimensions.answerFontSize.value - 2).sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) Color.White else {
                            if (isDarkTheme) Color.White.copy(alpha = 0.7f) else Color.Black.copy(alpha = 0.55f)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernBottomStats(
    timeLeft: Int,
    correctQuestionCount: Int,
    questionNumber: Int,
    dimensions: ResponsiveDimensions
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimensions.cardSpacing)
    ) {
        // Timer Card
        ModernStatCard(
            title = "Time",
            value = "${timeLeft}s",
            isUrgent = timeLeft <= 3,
            isDarkTheme = isDarkTheme,
            dimensions = dimensions,
            modifier = Modifier.weight(1f)
        )

        // Score Card — toplamı anında questionNumber olarak göster
        ModernStatCard(
            title = "Score",
            value = "$correctQuestionCount/${maxOf(1, questionNumber)-1}",
            isUrgent = false,
            isDarkTheme = isDarkTheme,
            dimensions = dimensions,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ModernStatCard(
    title: String,
    value: String,
    isUrgent: Boolean,
    isDarkTheme: Boolean,
    dimensions: ResponsiveDimensions,
    modifier: Modifier = Modifier
) {
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
            containerColor = if (isDarkTheme) Color.White.copy(alpha = 0.12f)
            else Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .scale(scale)
            .height(dimensions.statCardHeight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = dimensions.titleFontSize,
                fontWeight = FontWeight.Medium,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.8f)
                else Color.Black.copy(alpha = 0.7f),
                maxLines = 1
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
                    color = if (isUrgent) Color(0xFFE57373) else {
                        if (isDarkTheme) Color.White else Color.Black
                    },
                    maxLines = 1
                )
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
                            Color(0xFF0F0F23),
                            Color(0xFF1A1A2E),
                            Color(0xFF16213E)
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
                color = if (isDarkTheme) Color.White.copy(alpha = 0.8f)
                else Color.Black.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ModernCustomDialog(
    onDismiss: () -> Unit,
    onRestart: () -> Unit,
    score: Int,
    dimensions: ResponsiveDimensions
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

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
                    containerColor = MaterialTheme.colorScheme.primary,
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
                onClick = { onDismiss() },
                shape = RoundedCornerShape(dimensions.cardRadius),
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
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Trophy icon burada istersen açarsın
                // Icon(
                //     imageVector = Icons.Filled.EmojiEvents,
                //     contentDescription = "Trophy",
                //     tint = Color.White,
                //     modifier = Modifier.size(24.dp)
                // )
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
                    color = if (isDarkTheme) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f)
                )
                Text(
                    "$score / 10",
                    fontSize = dimensions.valueFontSize,
                    fontWeight = FontWeight.Bold,
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }
        }
    )
}
