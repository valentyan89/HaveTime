package com.example.havetime.domain.usecase

import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.domain.model.TimeInterval
import kotlinx.coroutines.flow.Flow

class SaveIntervalUseCase(private val repository: TodoRepository) {
    operator fun invoke(interval: TimeInterval): Flow<Unit> = repository.saveInterval(interval)
}