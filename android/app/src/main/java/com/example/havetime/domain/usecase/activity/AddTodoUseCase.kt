package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.repository.RemindManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class AddTodoUseCase(private val repository: ActivityRepository, private val remindManager: RemindManager) {
    operator fun invoke(todo: Activity): Flow<Unit> {
        return repository.addTodo(todo).onEach {
                remindManager.scheduleRemind(
                    taskId = todo.id,
                    taskTitle = todo.title,
                    startTimeMillis = todo.timeInterval.startTime
                )
            }
    }
}