package com.example.havetime.domain.usecase.activity

import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

class AddTodoUseCase(private val repository: ActivityRepository) {
    operator fun invoke(todo: Activity): Flow<Unit> = repository.addTodo(todo)
}
