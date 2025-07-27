package com.example.quizzy.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Screen
import com.example.quizzy.data.model.Difficulty
import kotlin.math.absoluteValue

@Composable
fun MainScreen(
    navController: NavHostController
) {
    val colorScheme = MaterialTheme.colorScheme
    var isCategorySelected by remember { mutableStateOf(false) }
    var selectedCategory by remember{mutableStateOf(Category.FILM)}
    var selectedDifficulty by remember { mutableStateOf(Difficulty.HARD) }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.Settings.route)
                },
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = colorScheme.background
        ) {
            if (!isCategorySelected){
                CategorySelection(colorScheme,navController){
                    selectedCategory = it
                    isCategorySelected = true
                }
            }else{
                DifficultySelection(colorScheme,navController){
                    selectedDifficulty = it
                    navController.navigate(Screen.Quiz.passCategoryAndDifficulty(selectedCategory,selectedDifficulty))
                }
            }

        }
    }
}

@Composable
fun CategorySelection(colorScheme : ColorScheme,navController : NavHostController,
                      onCategorySelected : (Category) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Category",
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onBackground
        )

        CustomButton("Film", Color(0xFFDA773E)) {
            onCategorySelected(Category.FILM)
        }
        CustomButton("Series", Color(0xFF1DE9B6)) {
            onCategorySelected(Category.SERIES)
        }
        CustomButton("Games", Color(0xFF569132)) {
            onCategorySelected(Category.GAMES)
        }
        CustomButton("Sport", Color(0xFFE1C491)) {
            onCategorySelected(Category.SPORT)
        }
    }
}

@Composable
fun DifficultySelection(colorScheme : ColorScheme,navController : NavHostController,
                        onDifficultySelected : (Difficulty) -> Unit
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
            color = colorScheme.onBackground
        )

        CustomButton("Easy", Color(0xFF81C784)) {
            onDifficultySelected(Difficulty.EASY)
        }
        CustomButton("Medium", Color(0xFFFFD54F)) {
            onDifficultySelected(Difficulty.MEDIUM)
        }
        CustomButton("Hard", Color(0xFFE57373)) {
            onDifficultySelected(Difficulty.HARD)
        }
    }
}



@Composable
fun CustomButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = text, fontSize = 20.sp)
    }
}
