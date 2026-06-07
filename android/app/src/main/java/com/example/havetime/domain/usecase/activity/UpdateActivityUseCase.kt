package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.repository.RemindManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class UpdateActivityUseCase(private val repository: ActivityRepository, private val remindManager: RemindManager) {
    operator fun invoke(activity: Activity): Flow<Unit> {
        return repository.updateActivity(activity).onEach {
            remindManager.cancelRemind(activity.id)
            remindManager.scheduleRemind(
                taskId = activity.id,
                taskTitle = activity.title,
                startTimeMillis = activity.timeInterval.startTime
            )
        }
    }
}