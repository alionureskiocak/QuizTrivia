package com.example.quizzy.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quizzy.data.model.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            SetUpNavigation(navController)

        }
    }
}

@Composable
fun SetUpNavigation(navController : NavHostController) {
    NavHost(navController = navController,
        startDestination = Screen.Home.route){
        composable(Screen.Home.route){
            MainScreen(navController = navController)
        }

        composable(Screen.Quiz.route, arguments = listOf(
            navArgument("difficulty") {type = NavType.StringType}
        )) {
            val difficulty = it.arguments?.getString("difficulty") ?:""
            QuizScreen(difficulty = difficulty)
        }
    }

}