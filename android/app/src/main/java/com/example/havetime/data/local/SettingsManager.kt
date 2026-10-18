package com.example.havetime.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.havetime.data.mapper.toLanguage
import com.example.havetime.data.mapper.toLocation
import com.example.havetime.domain.model.settingsPrefs.AppSettings
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SettingsManager @Inject constructor(@Named("settings_store") private val dataStore: DataStore<Preferences>) {
    private companion object {
        val LOCATION_MODE_KEY = stringPreferencesKey("location_mode")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
    }

    suspend fun saveLocationMode(locationMode: String) {
        dataStore.edit { preferences ->
            preferences[LOCATION_MODE_KEY] = locationMode
        }
    }

    suspend fun saveLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun saveDynamicColor(dynamicColor: Boolean) {
        dataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = dynamicColor
        }
    }

    val appSettings = dataStore.data.map { preferences ->
        AppSettings(
            location = preferences[LOCATION_MODE_KEY].toLocation(),
            language = preferences[LANGUAGE_KEY].toLanguage(),
            dynamicColor = preferences[DYNAMIC_COLOR_KEY] ?: false,
            notificationsEnabled = true
        )
    }

}
