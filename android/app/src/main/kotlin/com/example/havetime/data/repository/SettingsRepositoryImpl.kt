package com.example.havetime.data.repository

import com.example.havetime.data.local.SettingsManager
import com.example.havetime.domain.model.settingsPrefs.AppSettings
import com.example.havetime.domain.model.settingsPrefs.ColorMode
import com.example.havetime.domain.model.settingsPrefs.Language
import com.example.havetime.domain.model.settingsPrefs.Location
import com.example.havetime.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val settingsManager: SettingsManager) : SettingsRepository {
    override suspend fun setLanguage(language: Language) {
        settingsManager.saveLanguage(language.name)
    }

    override suspend fun setLocation(locationMode: Location) {
        settingsManager.saveLocationMode(locationMode.name)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        TODO()
    }

    override suspend fun setDynamicColor(enabled: Boolean) {
        settingsManager.saveDynamicColor(enabled)
    }

    override suspend fun setColorMode(colorMode: ColorMode) {
        settingsManager.saveColorMode(colorMode.name)
    }

    override val settings: Flow<AppSettings> = settingsManager.appSettings
}
