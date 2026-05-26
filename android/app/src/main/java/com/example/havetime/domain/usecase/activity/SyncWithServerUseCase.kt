package com.example.havetime.domain.usecase.activity

import com.example.havetime.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

class SyncWithServerUseCase(private val repository: ActivityRepository) {
    operator fun invoke(): Flow<Unit> = repository.syncWithServer()
}
