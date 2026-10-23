package com.example.havetime.domain.usecase.settings

import com.example.havetime.domain.repository.SettingsRepository

class SetNotificationsEnabledUseCase(private val repository: SettingsRepository) {
    suspend operator fun invoke(notificationsEnabled: Boolean) =
        repository.setNotificationsEnabled(notificationsEnabled)
}
