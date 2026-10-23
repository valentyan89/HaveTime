package com.example.havetime.domain.repository

import com.example.havetime.domain.model.settingsPrefs.AppSettings
import com.example.havetime.domain.model.settingsPrefs.Language
import com.example.havetime.domain.model.settingsPrefs.Location
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setLanguage(language: Language)

    suspend fun setLocation(locationMode: Location)

    suspend fun setNotificationsEnabled(enabled: Boolean)

    suspend fun setDynamicColor(enabled: Boolean)

    val settings: Flow<AppSettings>
}
