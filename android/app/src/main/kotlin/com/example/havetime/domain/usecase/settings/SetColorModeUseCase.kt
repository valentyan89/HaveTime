package com.example.havetime.domain.usecase.settings

import com.example.havetime.domain.model.settingsPrefs.ColorMode
import com.example.havetime.domain.repository.SettingsRepository

class SetColorModeUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(colorMode: ColorMode) = repository.setColorMode(colorMode)
}