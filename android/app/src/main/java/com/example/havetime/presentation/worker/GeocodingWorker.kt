package com.example.havetime.presentation.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.havetime.domain.usecase.GetAndSaveAddressUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GeocodingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getAndSaveAddressUseCase: GetAndSaveAddressUseCase
) : CoroutineWorker(context, workerParams){
    override suspend fun doWork(): Result {
        val activityId = inputData.getInt(KEY_ACTIVITY_ID, -1)
        Log.d("RRR", "Воркер принял Activity ID = $activityId")
        if (activityId == -1) return Result.failure()

        return try {
            val isSuccess = getAndSaveAddressUseCase(activityId)
            Log.d("RRR", "Результат выполнения Use Case = $isSuccess")
            if (isSuccess) {
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("RRR", "Ошибка внутри воркера!", e)
            Result.retry()
        }
    }

    companion object {
        const val KEY_ACTIVITY_ID = "key_activity_id"
    }
}