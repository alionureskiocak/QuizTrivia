package com.example.quizzy.ui.theme

import android.app.Activity
import android.content.res.Resources
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0X12121200),      // Açık yeşil - koyu mod için yumuşak bir ton
    onPrimary = Color.Black,
    secondary = Color(0xFF37474F),    // Maviye dönük koyu gri
    onSecondary = Color.White,
    background = Color(0xFF1C1C1C),   // Koyu zemin
    surface = Color(0xFF1E1E1E),      // Kart zeminleri
    onSurface = Color.White,
    error = Color(0xFFEF5350),
    onError = Color.Black
)


private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50),      // Yeşil - Buton ve öne çıkan öğeler için
    onPrimary = Color.White,          // Primary üstündeki yazılar için
    secondary = Color(0xFFB0BEC5),    // Gri ton - ikincil butonlar, arkaplan parçaları
    onSecondary = Color.Black,
    background = Color(0xFFF5F5F5),   // Hafif açık gri - genel zemin
    surface = Color.White,            // Kartlar, kutular
    onSurface = Color.Black,
    error = Color(0xFFD32F2F),
    onError = Color.White
)


@Composable
fun QuizzyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}