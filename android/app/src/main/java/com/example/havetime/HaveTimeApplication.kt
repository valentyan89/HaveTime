package com.example.havetime

import android.app.Application
import androidx.room.Room
import com.example.havetime.data.local.ActivityDataBase
import com.example.havetime.data.local.TokenManager
import com.example.havetime.data.remote.api.AuthApi
import com.example.havetime.data.remote.api.ActivityApi
import com.example.havetime.data.remote.client.KtorClient
import com.example.havetime.data.repository.ActivityRepositoryImpl
import com.example.havetime.data.repository.UserRepositoryImpl
import com.example.havetime.domain.repository.ActivityRepository
import com.example.havetime.domain.repository.UserRepository
import com.example.havetime.util.Constants
import org.osmdroid.config.Configuration

class HaveTimeApplication : Application() {

    lateinit var activityRepository: ActivityRepository
    lateinit var userRepository: UserRepository
    lateinit var tokenManager: TokenManager

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            this,
            ActivityDataBase::class.java,
            Constants.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

        tokenManager = TokenManager(this)
        val httpClient = KtorClient.client
        val authApi = AuthApi(httpClient)
        val activityApi = ActivityApi(httpClient)

        activityRepository = ActivityRepositoryImpl(
            api = activityApi,
            activityDao = database.activityDao(),
            userDao = database.userDao()
        )

        userRepository = UserRepositoryImpl(
            userDao = database.userDao(),
            api = authApi,
            tokenManager = tokenManager
        )

        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this))
    }
}
