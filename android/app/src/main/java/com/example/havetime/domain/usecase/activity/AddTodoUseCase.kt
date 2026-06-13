package com.example.havetime.domain.usecase.activity

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.repository.RemindManager
import com.example.havetime.presentation.worker.GeocodingWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

class AddTodoUseCase(private val repository: ActivityRepository, private val remindManager: RemindManager, private val worker: WorkManager) {
    operator fun invoke(todo: Activity): Flow<Unit> {
        return repository.addTodo(todo).onEach { actualId ->
            remindManager.scheduleRemind(
                taskId = actualId,
                taskTitle = todo.title,
                startTimeMillis = todo.timeInterval.startTime
            )

            val lat = todo.location?.latitude
            val lon = todo.location?.longitude

            if (lat != null && lon != null) {
                val inputData = Data.Builder()
                    .putInt(GeocodingWorker.KEY_ACTIVITY_ID, actualId)
                    .build()

                val geocodingWorkRequest = OneTimeWorkRequestBuilder<GeocodingWorker>()
                    .setInputData(inputData)
                    .build()

                worker.enqueue(geocodingWorkRequest)
            }
        }.map { Unit }
    }
}