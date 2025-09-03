package com.alionur.quizzy.presentation.ui.main

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty
import com.alionur.quizzy.data.model.Screen

// İnternet kontrolü
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
    var selectedCategory by remember { mutableStateOf(Category.FILM) }
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
        // Sağ üst Ayarlar butonu
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
                    icon = Icons.Rounded.ArrowBack
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

// GlassButton ve ModernCard + Grid fonksiyonları aynen buraya eklenir
// (Senin daha önce verdiğin kodla birebir)

// Küçük, yuvarlak ve cam efekti ile temaya uygun buton
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
    val topPadding = screenHeight * 0.03f // ekranın %6'sı kadar boşluk
    Card(
        modifier = modifier
            .scale(scale)
            .padding(top = topPadding)
            .size(40.dp) // Küçük boyut
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
fun ModernCategoryGrid(
    onCategorySelected: (Category) -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
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

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernCard(
                    icon = "🎬",
                    label = "Film",
                    description = "Movies & Shows",
                    gradient = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    ),
                    modifier = Modifier.weight(1f).height(140.dp),
                    onClick = { onCategorySelected(Category.FILM) }
                )
                ModernCard(
                    icon = "📺",
                    label = "Series",
                    description = "TV Shows",
                    gradient = listOf(
                        Color(0xFF4FACFE),
                        Color(0xFF00F2FE)
                    ),
                    modifier = Modifier.weight(1f).height(140.dp),
                    onClick = { onCategorySelected(Category.SERIES) }
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModernCard(
                    icon = "🎮",
                    label = "Gaming",
                    description = "Video Games",
                    gradient = listOf(
                        Color(0xFF43E97B),
                        Color(0xFF38F9D7)
                    ),
                    modifier = Modifier.weight(1f).height(140.dp),
                    onClick = { onCategorySelected(Category.GAMES) }
                )
                ModernCard(
                    icon = "🏆",
                    label = "Sports",
                    description = "Athletics",
                    gradient = listOf(
                        Color(0xFFFA709A),
                        Color(0xFFFEE140)
                    ),
                    modifier = Modifier.weight(1f).height(140.dp),
                    onClick = { onCategorySelected(Category.SPORT) }
                )
            }
        }
    }
}

@Composable
fun ModernDifficultyGrid(
    onDifficultySelected: (Difficulty) -> Unit
) {
    val isDarkTheme = !MaterialTheme.colorScheme.surface.luminance().let { it > 0.5f }

    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Difficulty",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = if (isDarkTheme) Color.White else Color.Black
        )

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ModernCard(
                icon = "\uD83C\uDF31",
                label = "Beginner",
                description = "Perfect for starters",
                gradient = listOf(
                    Color(0xFF56AB2F),
                    Color(0xFFA8E6CF)
                ),
                modifier = Modifier.fillMaxWidth(), // ✅ height yok artık
                onClick = { onDifficultySelected(Difficulty.HARD) },
                labelFontSize = 20.sp,
                descriptionFontSize = 14.sp
            )
            ModernCard(
                icon = "\uD83D\uDD25",
                label = "Intermediate",
                description = "Challenge yourself",
                gradient = listOf(
                    Color(0xFFFF8008),
                    Color(0xFFFFC837)
                ),
                modifier = Modifier.fillMaxWidth(), // ✅ height yok artık
                onClick = { onDifficultySelected(Difficulty.HARD) },
                labelFontSize = 20.sp,
                descriptionFontSize = 14.sp
            )
            ModernCard(
                icon = "⚡",
                label = "Expert",
                description = "For the brave ones",
                gradient = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)),
                modifier = Modifier.fillMaxWidth(), // ✅ height yok artık
                onClick = { onDifficultySelected(Difficulty.HARD) },
                labelFontSize = 20.sp,
                descriptionFontSize = 14.sp
            )
           /* ModernCard(
                icon = "\uD83C\uDF31",
                label = "Beginner",
                description = "Perfect for starters",
                gradient = listOf(
                    Color(0xFF56AB2F),
                    Color(0xFFA8E6CF)
                ),
                modifier = Modifier.fillMaxWidth().height(100.dp),
                onClick = { onDifficultySelected(Difficulty.EASY) },
                labelFontSize = 20.sp,
                descriptionFontSize = 14.sp
            )

            ModernCard(
                icon = "\uD83D\uDD25",
                label = "Intermediate",
                description = "Challenge yourself",
                gradient = listOf(
                    Color(0xFFFF8008),
                    Color(0xFFFFC837)
                ),
                modifier = Modifier.fillMaxWidth().height(100.dp),
                onClick = { onDifficultySelected(Difficulty.MEDIUM) },
                labelFontSize = 20.sp,
                descriptionFontSize = 14.sp
            )

            ModernCard(
                icon = "⚡",
                label = "Expert",
                description = "For the brave ones",
                gradient = listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0)),
                modifier = Modifier.fillMaxWidth(), // ✅ height yok artık
                onClick = { onDifficultySelected(Difficulty.HARD) },
                labelFontSize = 20.sp,
                descriptionFontSize = 14.sp
            )*/

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
    labelFontSize: TextUnit = 18.sp,
    descriptionFontSize: TextUnit = 13.sp
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
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.linearGradient(gradient))
                .padding(vertical = 20.dp, horizontal = 16.dp) // daha dengeli padding
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp), // elemanlar arası boşluk
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = icon,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = label,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = Color.White,
                        fontSize = labelFontSize,
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
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = descriptionFontSize,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


