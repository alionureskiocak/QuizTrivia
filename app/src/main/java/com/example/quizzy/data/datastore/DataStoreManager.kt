package com.example.quizzy.data.datastore

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
        val DARK_MODE = stringPreferencesKey("system")
    }
    val themeMode : Flow<String> = dataStore.data
        .map { it[DARK_MODE] ?: "system" }

    suspend fun setThemeMode(themeMode : String){
        dataStore.edit { settings ->
            settings[DARK_MODE] = themeMode
        }
    }

}