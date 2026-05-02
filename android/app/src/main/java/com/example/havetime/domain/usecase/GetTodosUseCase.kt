package com.example.havetime.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.domain.model.TodoItem

class GetTodosUseCase(private val repository: TodoRepository) {
    operator fun invoke(): Flow<List<TodoItem>> = repository.getTodos()
}