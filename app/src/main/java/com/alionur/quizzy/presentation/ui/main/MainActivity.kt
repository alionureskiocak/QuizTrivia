package com.alionur.quizzy.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alionur.quizzy.data.datastore.DataStoreManager
import com.alionur.quizzy.data.model.Screen
import com.alionur.quizzy.ui.theme.QuizzyTheme
import com.alionur.quizzy.data.model.Category
import com.alionur.quizzy.data.model.Difficulty
import com.alionur.quizzy.presentation.ui.quiz.QuizScreen
import com.alionur.quizzy.presentation.ui.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var dataStoreManager : DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeMode by rememberSaveable { mutableStateOf("system") }

            LaunchedEffect(Unit) {
                dataStoreManager.themeMode.collect {
                    themeMode = it
                }
            }

            QuizzyTheme(themeMode = themeMode) {
                val navController = rememberNavController()
                SetUpNavigation(
                    navController = navController,
                    onThemeSelected = { selectedTheme ->
                        themeMode = selectedTheme

                        lifecycleScope.launch {
                            dataStoreManager.setThemeMode(selectedTheme)
                        }
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

            QuizScreen(navController = navController,category = category, difficulty = difficulty)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onThemeSelected = onThemeSelected)
        }
    }
}
