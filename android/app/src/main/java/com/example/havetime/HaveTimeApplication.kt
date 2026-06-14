package com.example.havetime

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.example.havetime.presentation.worker.SyncDataWorker
import com.example.havetime.presentation.worker.WidgetUpdateWorker
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.IConfigurationProvider
import javax.inject.Inject

@HiltAndroidApp
class HaveTimeApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var osmConfig: IConfigurationProvider
    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "application_reminder"
            val channelName = "Напоминания о задачах"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Уведомления за час до активности"
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        SyncDataWorker.plannedSyncWorker(this)
        WidgetUpdateWorker.scheduleBackgroundUpdate(this)
    }
}