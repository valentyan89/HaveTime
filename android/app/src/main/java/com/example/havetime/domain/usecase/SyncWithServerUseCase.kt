package com.example.havetime.domain.usecase

import com.example.calendar.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class SyncWithServerUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<Unit> = repository.syncWithServer()
}