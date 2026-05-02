package com.example.havetime.domain.usecase

import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

class DeleteTodoUseCase(private val repository: TodoRepository) {
    operator fun invoke(id: Int): Flow<Unit> = repository.deleteTodo(id)
}