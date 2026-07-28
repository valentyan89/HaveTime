package com.example.havetime.presentation.worker

import android.content.Context
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.havetime.presentation.widget.HaveTimeWidget
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit


@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
): CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        HaveTimeWidget().updateAll(applicationContext)

        return Result.success()
    }

    companion object {
        private const val WORKER_NAME = "update_widget_worker"
        private const val INTERVAL_UPDATE = 5L

        fun scheduleBackgroundUpdate(context: Context){
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val widgetWork = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
                repeatInterval = INTERVAL_UPDATE,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                uniqueWorkName = WORKER_NAME,
                existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
                request = widgetWork
            )
        }
    }
}