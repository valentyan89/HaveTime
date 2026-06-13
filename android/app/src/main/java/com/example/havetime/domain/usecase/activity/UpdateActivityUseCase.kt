package com.example.havetime.domain.usecase.activity

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.repository.RemindManager
import com.example.havetime.presentation.worker.GeocodingWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class UpdateActivityUseCase(private val repository: ActivityRepository, private val remindManager: RemindManager, private val worker: WorkManager) {
    operator fun invoke(activity: Activity): Flow<Unit> {
        return repository.updateActivity(activity).onEach {
            remindManager.cancelRemind(activity.id)
            remindManager.scheduleRemind(
                taskId = activity.id,
                taskTitle = activity.title,
                startTimeMillis = activity.timeInterval.startTime
            )

            val lat = activity.location?.latitude
            val lon = activity.location?.longitude

            if (lat != null && lon != null) {
                val inputData = Data.Builder()
                    .putInt(GeocodingWorker.KEY_ACTIVITY_ID, activity.id)
                    .build()

                val geocodingWorkRequest = OneTimeWorkRequestBuilder<GeocodingWorker>()
                    .setInputData(inputData)
                    .build()

                worker.enqueue(geocodingWorkRequest)
            }
        }
    }
}