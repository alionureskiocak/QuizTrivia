package com.alionur.quizzy.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alionur.quizzy.data.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val themeMode = dataStoreManager.themeMode

    fun setThemeMode(value : String){
        viewModelScope.launch {
            dataStoreManager.setThemeMode(value)
        }
    }
}