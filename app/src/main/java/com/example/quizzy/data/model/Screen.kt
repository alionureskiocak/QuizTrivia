package com.example.quizzy.data.model

import com.example.quizzy.util.Difficulty

sealed class Screen(val route : String) {

    object Home : Screen(route = "home_screen")
    object Quiz : Screen(route = "quiz_screen/{difficulty}"){
        fun passDifficulty(diff : Difficulty?) = "quiz_screen/$diff"
    }
    object Settings : Screen(route = "settings_screen")
}