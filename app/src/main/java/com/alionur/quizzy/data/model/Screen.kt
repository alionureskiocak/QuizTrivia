package com.alionur.quizzy.data.model

sealed class Screen(val route : String) {

    object Home : Screen(route = "home_screen")
    object Quiz : Screen(route = "quiz_screen/{category}/{difficulty}"){
        fun passCategoryAndDifficulty(category : Category, difficulty : Difficulty?) =
            "quiz_screen/${category.name}/${difficulty?.name}"
    }
    object Settings : Screen(route = "settings_screen")
}