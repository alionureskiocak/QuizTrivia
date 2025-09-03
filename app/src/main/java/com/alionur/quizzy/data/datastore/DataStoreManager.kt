package com.alionur.quizzy.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStore : DataStore<Preferences>
) {

    companion object{
        val THEME_MODE = stringPreferencesKey("dark")
    }
    val themeMode : Flow<String> = dataStore.data
        .map { it[THEME_MODE] ?: "dark" }

    suspend fun setThemeMode(themeMode : String){
        dataStore.edit { settings ->
            settings[THEME_MODE] = themeMode
        }
    }

}