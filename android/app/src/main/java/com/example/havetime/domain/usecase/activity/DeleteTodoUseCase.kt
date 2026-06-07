package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

class DeleteTodoUseCase(private val repository: ActivityRepository) {
    operator fun invoke(id: Int): Flow<Unit> = repository.deleteTodo(id)
}