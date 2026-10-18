package com.example.havetime.domain.usecase.settings

import com.example.havetime.domain.repository.SettingsRepository

class SetDynamicColorUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repository.setDynamicColor(enabled)
}