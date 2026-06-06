package com.example.havetime.presentation.worker

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.havetime.domain.usecase.activity.SyncWithServerUseCase

class SyncWorkerFactory(private val syncWithServerUseCase: SyncWithServerUseCase) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            SyncDataWorker::class.java.name -> {
                SyncDataWorker(appContext, workerParameters, syncWithServerUseCase)
            }
            else -> null
        }
    }
}