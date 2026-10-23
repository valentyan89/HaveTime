package com.example.havetime.domain.usecase.settings

import com.example.havetime.domain.model.settingsPrefs.AppSettings
import com.example.havetime.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(private val repository: SettingsRepository) {
    operator fun invoke(): Flow<AppSettings> = repository.settings
}
