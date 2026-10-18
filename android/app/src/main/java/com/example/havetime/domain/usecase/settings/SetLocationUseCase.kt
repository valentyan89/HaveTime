package com.example.havetime.domain.usecase.settings

import com.example.havetime.domain.model.settingsPrefs.Location
import com.example.havetime.domain.repository.SettingsRepository

class SetLocationUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(location: Location) = repository.setLocation(location)
}