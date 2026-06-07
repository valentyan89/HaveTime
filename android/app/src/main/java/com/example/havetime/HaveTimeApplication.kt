package com.example.havetime

import android.app.Application
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
import com.example.havetime.data.repository.UserRepositoryImpl
import com.example.havetime.domain.repository.DateRepository
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

    override fun onCreate() {
        super.onCreate()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(SyncWorkerFactory(syncUseCase))
            .build()
}