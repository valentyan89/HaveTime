package com.example.havetime

import android.app.Application
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
    lateinit var todoRepository: ActivityRepository
    lateinit var userRepository: UserRepository
    lateinit var dateRepository: DateRepository
    lateinit var tokenManager: TokenManager
    lateinit var syncUseCase: SyncWithServerUseCase

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            this,
            ActivityDataBase::class.java,
            "havetime_database"
        ).build()
        tokenManager = TokenManager(this)
        val httpClient = KtorClient.client
        val authApi = AuthApi(httpClient)
        val activityApi = ActivityApi(httpClient)


        todoRepository = ActivityRepositoryImpl(
            todoDao = database.todoDao(),
            userDao = database.userDao(),
            api = activityApi
        )

        userRepository = UserRepositoryImpl(
            userDao = database.userDao(),
            api = authApi,
            tokenManager = tokenManager
        )

        dateRepository = DateRepositoryImpl()

        syncUseCase = SyncWithServerUseCase(todoRepository)
    }

    override val workManagerConfiguration: Configuration
        get() {
            if (!::syncUseCase.isInitialized) {
                val database = Room.databaseBuilder(
                    this,
                    ActivityDataBase::class.java,
                    "havetime_database"
                ).build()

                val activityApi = ActivityApi(KtorClient.client)

                todoRepository = ActivityRepositoryImpl(
                    todoDao = database.todoDao(),
                    userDao = database.userDao(),
                    api = activityApi
                )
                syncUseCase = SyncWithServerUseCase(todoRepository)
            }

            return Configuration.Builder()
                .setWorkerFactory(SyncWorkerFactory(syncUseCase))
                .build()
        }
}