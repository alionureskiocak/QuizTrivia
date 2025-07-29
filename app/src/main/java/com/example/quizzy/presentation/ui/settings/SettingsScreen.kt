package com.example.quizzy.presentation.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen(onThemeSelected : (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
        ) {
        Button(onClick ={
            onThemeSelected("light")
        }
        ) {
            Text("Light Mode",fontSize = 24.sp)
        }

        Button(onClick ={
            onThemeSelected("Dark")
        }
        ) {
            Text("Dark Mode",fontSize = 24.sp)
        }

        Button(onClick ={
            onThemeSelected("system")
        }
        ) {
            Text("System Default",fontSize = 24.sp)
        }
    }
}