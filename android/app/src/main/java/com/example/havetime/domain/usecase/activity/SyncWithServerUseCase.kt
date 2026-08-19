package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository

class SyncWithServerUseCase(private val repository: ActivityRepository) {
    suspend operator fun invoke(): Result<Unit> = repository.syncWithServer()
}