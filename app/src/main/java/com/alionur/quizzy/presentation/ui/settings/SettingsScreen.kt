package com.alionur.quizzy.presentation.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onThemeSelected: (String) -> Unit
) {
    val themeMode = viewModel.themeMode.collectAsState(initial = "system")

    val isSystemSelected = themeMode.value == "system"
    val isDarkSelected = themeMode.value == "dark"

    Surface(color = colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, top = 48.dp),

            horizontalAlignment = Alignment.CenterHorizontally

            ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )

            // Sistem temasını kullan seçeneği
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "Use System Theme")
                    Text(
                        text = "Base on device settings",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Checkbox(
                    checked = isSystemSelected,
                    onCheckedChange = { checked ->
                        if (checked) {
                            viewModel.setThemeMode("system")
                            onThemeSelected("system")
                        } else {
                            viewModel.setThemeMode("light") // sistem devre dışıysa default light
                            onThemeSelected("light")
                        }
                    }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "Dark Mode")
                    Text(
                        text = "Set Light/Dark Theme Manually",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isDarkSelected,
                    onCheckedChange = { checked ->
                        val mode = if (checked) "dark" else "light"
                        viewModel.setThemeMode(mode)
                        onThemeSelected(mode)
                    },
                    enabled = !isSystemSelected // sistem teması seçiliyse switch pasif
                )
            }
        }
    }

}
