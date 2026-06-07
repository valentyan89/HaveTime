package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.repository.RemindManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class DeleteTodoUseCase(private val repository: ActivityRepository, private val remindManager: RemindManager) {
    operator fun invoke(id: Int): Flow<Unit> {
        return repository.deleteTodo(id).onEach {
                remindManager.cancelRemind(id)
            }
    }
}