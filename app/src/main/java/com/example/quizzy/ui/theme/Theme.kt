package com.example.quizzy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    background = Color(0xFF121212), // Daha ferah koyuluk
    surface = Color(0xFF1E1E1E),    // Hafif yumuşak koyu
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F67AF),
    onPrimary = Color.White,
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color.Black,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFD32F2F),
    onError = Color.White
)

@Composable
fun QuizzyTheme(
    themeMode: String,
    content: @Composable () -> Unit
) {
    val isDarkTheme = when (themeMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDarkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
