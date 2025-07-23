package com.example.quizzy.data.model

sealed class Screen(val route : String) {

    object Home : Screen(route = "home_screen")
    object Quiz : Screen(route = "quiz_screen/{difficulty}"){
        fun passDifficulty(diff : String) = "quiz_screen/$diff"
    }
    object Settings : Screen(route = "settings_screen")
}