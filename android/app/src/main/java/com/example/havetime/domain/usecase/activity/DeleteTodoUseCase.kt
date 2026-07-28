package com.example.havetime.domain.usecase.activity

import android.util.Log
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.repository.RemindManager
import com.example.havetime.domain.repository.WidgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeleteTodoUseCase(
    private val repository: ActivityRepository,
    private val remindManager: RemindManager,
    private val widgetRepository: WidgetRepository
) {
    operator fun invoke(id: Int): Flow<Unit> {
        return repository.deleteTodo(id).map {
                remindManager.cancelRemind(id)
                Log.d("RRR", "delete")
                widgetRepository.updateWidget()
                Unit
            }
    }
}