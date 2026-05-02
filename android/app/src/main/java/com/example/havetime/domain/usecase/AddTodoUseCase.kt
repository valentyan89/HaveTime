package com.example.havetime.domain.usecase

import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

class AddTodoUseCase(private val repository: TodoRepository) {
    operator fun invoke(todo: TodoItem): Flow<Unit> = repository.addTodo(todo)
}