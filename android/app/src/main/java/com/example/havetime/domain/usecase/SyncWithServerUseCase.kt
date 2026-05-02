package com.example.havetime.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.domain.model.TimeInterval

class SyncWithServerUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<Unit> = repository.syncWithServer()
}