package com.example.quizzy.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizzy.data.model.Screen
import com.example.quizzy.ui.theme.QuizzyTheme
import com.example.quizzy.data.model.Category
import com.example.quizzy.data.model.Difficulty
import com.example.quizzy.presentation.ui.quiz.QuizScreen
import com.example.quizzy.presentation.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeState = rememberSaveable { mutableStateOf("system") }

            QuizzyTheme(themeMode = themeState.value) {
                val navController = rememberNavController()
                SetUpNavigation(
                    navController = navController,
                    onThemeSelected = { selectedTheme ->
                        themeState.value = selectedTheme
                    }
                )
            }
        }
    }
}

@Composable
fun SetUpNavigation(
    navController: NavHostController,
    onThemeSelected: (String) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            MainScreen(navController = navController)
        }

        composable(
            Screen.Quiz.route,
            arguments = listOf(
                navArgument("category") { type = NavType.StringType },
                navArgument("difficulty") { type = NavType.StringType }
            )
        ) {
            val categoryString = it.arguments?.getString("category")
            val difficultyString = it.arguments?.getString("difficulty")
            val category = Category.valueOf(categoryString!!)
            val difficulty = Difficulty.valueOf(difficultyString.toString())

            QuizScreen(category = category, difficulty = difficulty)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onThemeSelected = onThemeSelected)
        }
    }
}
