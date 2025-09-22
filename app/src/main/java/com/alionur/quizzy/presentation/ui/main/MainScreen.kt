package com.alionur.quizzy.presentation.ui.main

import android.Manifest
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntSize
import androidx.navigation.NavHostController
import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty
import com.alionur.quizzy.data.model.Screen


data class Particle(
    var x: Float,
    var y: Float,
    var radius: Float,
    var color: Color,
    var alpha: Float,
    var velocityX: Float,
    var velocityY: Float
)
@Composable
fun ParticleBackground(modifier: Modifier = Modifier,number : Int) {

    val trigger = remember { mutableStateOf(0) }
    val surfaceColor = MaterialTheme.colorScheme.onSurface

    val particles = remember {

        List(number) { Particle(x = 0f, y = 0f, radius = 0f, color = surfaceColor, alpha =  0f, velocityX = 0f, velocityY = 0f) }
    }


    var size by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(key1 = size) {

        if (size == IntSize.Zero) return@LaunchedEffect


        particles.forEach { particle ->
            resetParticle(particle, size)
        }


        while (true) {
            particles.forEach { p ->

                p.x += p.velocityX
                p.y += p.velocityY

                p.alpha -= 0.004f


                if (p.x > size.width || p.x < 0 || p.y > size.height || p.y < 0 || p.alpha <= 0f) {
                    resetParticle(p, size)
                }
            }

            trigger.value++
            delay(16)
        }
    }

    // Çizim alanı
    Canvas(modifier = modifier.fillMaxSize()) {
        val forceRedraw = trigger.value


        if(size != this.size.toIntSize()){
            size = this.size.toIntSize()
        }

        particles.forEach { particle ->
            drawCircle(
                color = particle.color,
                center = Offset(particle.x, particle.y),
                radius = particle.radius,
                alpha = particle.alpha
            )
        }
    }
}

fun resetParticle(particle: Particle, size: IntSize) {
    val random = Random.Default
    val angle = random.nextDouble(0.0, 2.0 * Math.PI)
    val speed = random.nextFloat() * 1.2f + 0.2f //

    particle.apply {
        x = random.nextInt(0, size.width).toFloat()
        y = random.nextInt(0, size.height).toFloat()
        radius = random.nextInt(3, 10).toFloat()
        //color = if (isSystemInDarkTheme()) Color.White else Color.Black
        alpha = random.nextFloat() * 0.7f + 0.1f
        velocityX = (cos(angle) * speed).toFloat()
        velocityY = (sin(angle) * speed).toFloat()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    var isConnected by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isConnected = context.isInternetAvailable()
    }

    if (!isConnected) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No internet connection!\nPlease check your network.",
                color = Color.Red,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    MainScreenContent(navController)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreenContent(navController: NavHostController) {
    var isCategorySelected by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(Category.FILM_AND_TV) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.HARD) }

    BackHandler {
        if (isCategorySelected) isCategorySelected = false
    }

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
            )
    ) {

        ParticleBackground(number = 65)


        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
        ) {
            GlassButton(
                onClick = { navController.navigate(Screen.Settings.route) },
                icon = Icons.Outlined.Settings
            )
        }

        // Sol üst Geri butonu
        if (isCategorySelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp, start = 16.dp)
            ) {
                GlassButton(
                    onClick = { isCategorySelected = false },
                    icon = Icons.AutoMirrored.Rounded.ArrowBack
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quiz Trivia",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.headlineLarge.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Test your knowledge",
                fontSize = 16.sp,
                color = if (isDarkTheme) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedContent(
                targetState = isCategorySelected,
                transitionSpec = {
                    slideInHorizontally(
                        animationSpec = tween(500, easing = FastOutSlowInEasing),
                        initialOffsetX = { if (targetState) 300 else -300 }
                    ) + fadeIn() togetherWith slideOutHorizontally(
                        animationSpec = tween(500, easing = FastOutSlowInEasing),
                        targetOffsetX = { if (targetState) -300 else 300 }
                    ) + fadeOut()
                },
                label = "contentTransition"
            ) { showDifficulty ->
                if (!showDifficulty) {
                    ModernCategoryGrid(
                        onCategorySelected = {
                            selectedCategory = it
                            isCategorySelected = true
                        }
                    )
                } else {
                    ModernDifficultyGrid(
                        onDifficultySelected = {
                            selectedDifficulty = it
                            navController.navigate(
                                Screen.Quiz.passCategoryAndDifficulty(
                                    selectedCategory,
                                    selectedDifficulty
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR2)
@Composable
fun GlassButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val topPadding = screenHeight * 0.03f
    Card(
        modifier = modifier
            .scale(scale)
            .padding(top = topPadding)
            .size(40.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceVariant.copy(alpha = 0.6f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ModernDifficultyGrid(
    onDifficultySelected: (Difficulty) -> Unit
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Difficulty",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = if (isDarkTheme) Color.White else Color.Black
        )

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            DifficultyCard(
                icon = "🌱",
                label = "Beginner",
                description = "Perfect for starters",
                gradient = listOf(
                    Color(0xFF56AB2F),
                    Color(0xFFA8E6CF)
                ),
                onClick = { onDifficultySelected(Difficulty.EASY) }
            )
            DifficultyCard(
                icon = "🔥",
                label = "Intermediate",
                description = "Challenge yourself",
                gradient = listOf(
                    Color(0xFFFF8008),
                    Color(0xFFFFC837)
                ),
                onClick = { onDifficultySelected(Difficulty.MEDIUM) }
            )
            DifficultyCard(
                icon = "⚡",
                label = "Expert",
                description = "For the brave ones",
                gradient = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)),
                onClick = { onDifficultySelected(Difficulty.HARD) }
            )
        }
    }
}

@Composable
fun DifficultyCard(
    icon: String,
    label: String,
    description: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(gradient))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = icon,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = label,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.25f),
                                offset = Offset(2f, 2f),
                                blurRadius = 4f
                            )
                        )
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 16.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                }
            }
        }
    }
}
@Composable
fun ModernCategoryGrid(
    onCategorySelected: (Category) -> Unit
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Category",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = if (isDarkTheme) Color.White.copy(alpha = 0.95f) else Color.Black.copy(alpha = 0.9f)
        )

        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ModernCard(
                    icon = "🌏",
                    label = "General Knowledge",
                    description = "General Culture And Everyday Knowledge",
                    gradient = listOf(
                        Color(0xFF4FACFE),
                        Color(0xFF00F2FE)
                    ),

                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = { onCategorySelected(Category.GENERAL_KNOWLEDGE) }
                )
                ModernCard(
                    icon = "🎬",
                    label = "Film and TV",
                    description = "Movies & TV shows Worldwide",
                    gradient = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    ),

                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(), // CHANGED: from .height(180.dp)
                    onClick = { onCategorySelected(Category.FILM_AND_TV) }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ModernCard(
                    icon = "🗺️",
                    label = "Geography and History",
                    description = "Countries, Cities & Historical Events",
                    gradient = listOf(
                        Color(0xFF43E97B),
                        Color(0xFF38F9D7)
                    ),

                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = { onCategorySelected(Category.GEOGRAPHY_AND_HISTORY) }
                )
                ModernCard(
                    icon = "🏆",
                    label = "Sport and Leisure",
                    description = "Sport Branches & Leisure Activities",
                    gradient = listOf(
                        Color(0xFFFA709A),
                        Color(0xFFFEE140)
                    ),

                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    onClick = { onCategorySelected(Category.SPORT_AND_LEISURE) }
                )
            }
        }
    }
}


@Composable
fun ModernCard(
    icon: String,
    label: String,
    description: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interaction,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(gradient))
                .padding(vertical = 24.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = icon,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 36.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.25f),
                            offset = Offset(2f, 2f),
                            blurRadius = 4f
                        )
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}


@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun Context.isInternetAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val actNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
}