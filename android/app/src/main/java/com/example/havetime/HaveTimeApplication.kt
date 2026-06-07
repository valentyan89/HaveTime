package com.example.havetime

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.room.Room
import androidx.work.Configuration
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.local.ActivityDataBase
import com.example.havetime.data.local.TokenManager
import com.example.havetime.data.remote.api.ActivityApi
import com.example.havetime.data.remote.api.AuthApi
import com.example.havetime.data.remote.client.KtorClient
import com.example.havetime.data.repository.ActivityRepositoryImpl
import com.example.havetime.data.repository.DateRepositoryImpl
import com.example.havetime.data.repository.RemindManagerImpl
import com.example.havetime.data.repository.UserRepositoryImpl
import com.example.havetime.domain.repository.DateRepository
import com.example.havetime.domain.repository.RemindManager
import com.example.havetime.domain.repository.UserRepository
import com.example.havetime.domain.usecase.activity.SyncWithServerUseCase
import com.example.havetime.presentation.worker.SyncWorkerFactory

class HaveTimeApplication : Application(), Configuration.Provider {
    val tokenManager by lazy { TokenManager(this) }

    private val database by lazy {
        Room.databaseBuilder(
            this,
            ActivityDataBase::class.java,
            "havetime_database"
        ).build()
    }

    private val httpClient by lazy { KtorClient.client }
    private val authApi by lazy { AuthApi(httpClient) }
    private val activityApi by lazy { ActivityApi(httpClient) }

    val remindManager: RemindManager by lazy {
        RemindManagerImpl(this)
    }

    val todoRepository: ActivityRepository by lazy {
        ActivityRepositoryImpl(
            todoDao = database.todoDao(),
            userDao = database.userDao(),
            api = activityApi
        )
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(
            userDao = database.userDao(),
            todoDao = database.todoDao(),
            api = authApi,
            tokenManager = tokenManager
        )
    }

    val dateRepository: DateRepository by lazy {
        DateRepositoryImpl()
    }

    val syncUseCase by lazy {
        SyncWithServerUseCase(todoRepository)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "application_reminder"
            val channelName = "Напоминания о задачах"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Уведомления за час до активности"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(SyncWorkerFactory(syncUseCase))
            .build()
}