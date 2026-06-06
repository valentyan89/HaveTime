package com.example.havetime.presentation.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import java.util.concurrent.TimeUnit
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.havetime.HaveTimeApplication
import com.example.havetime.domain.usecase.activity.SyncWithServerUseCase

class SyncDataWorker(
    context: Context,
    params: WorkerParameters,
    private val syncWithServerUseCase: SyncWithServerUseCase
) : CoroutineWorker(context, params){
    override suspend fun doWork(): Result {
        Log.d("RRR", "воркер")
        return try{
            val result = syncWithServerUseCase()
            if (result.isSuccess){
                Result.success()
            } else {
                val error = result.exceptionOrNull()
                Result.retry()
            }
        } catch (e: Throwable){
            Result.retry()
        }
    }

    companion object{
        private const val WORKER_NAME = "sync_activity_worker"
        private const val INTERVAL_SYNC = 15L

        fun plannedSyncWorker(context: Context){
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncWork = PeriodicWorkRequestBuilder<SyncDataWorker>(
                repeatInterval = INTERVAL_SYNC,
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
                flexTimeInterval = 5L,
                flexTimeIntervalUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueWorkName = WORKER_NAME,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.REPLACE,
                request = syncWork
            )
        }

        fun cancelSyncWorker(context: Context){
            WorkManager.getInstance(context).cancelUniqueWork(WORKER_NAME)
        }
    }
}