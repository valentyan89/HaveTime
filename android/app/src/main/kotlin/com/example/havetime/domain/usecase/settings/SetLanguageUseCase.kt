package com.example.havetime.domain.usecase.settings

import com.example.havetime.domain.model.settingsPrefs.Language
import com.example.havetime.domain.repository.SettingsRepository

class SetLanguageUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(language: Language) = repository.setLanguage(language)
}
