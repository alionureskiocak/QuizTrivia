package com.example.quizzy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quizzy.data.model.Screen

@Composable
fun MainScreen(
    navController : NavHostController
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color(0xFF6200EE),
                contentColor = Color.White,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Difficulty",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color(0xFF333333)
                )

                DifficultyButton("Easy", Color(0xFF81C784)) {
                    navController.navigate(Screen.Quiz.passDifficulty("easy"))
                }
                DifficultyButton("Medium", Color(0xFFFFD54F)) {
                    navController.navigate(Screen.Quiz.passDifficulty("medium"))
                }
                DifficultyButton("Hard", Color(0xFFE57373)) {
                    navController.navigate(Screen.Quiz.passDifficulty("hard"))
                }
            }
        }
    }
}

@Composable
fun DifficultyButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}
