package com.example.havetime

import android.app.Application
import androidx.room.Room
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.local.ActivityDataBase
import com.example.havetime.data.repository.ActivityRepositoryImpl

class HaveTimeApplication : Application() {

    lateinit var activityRepository: ActivityRepository

    override fun onCreate() {
        super.onCreate()

        val database = ActivityDataBase.getDatabase(this)

        activityRepository = ActivityRepositoryImpl(
            activityDao = database.activityDao()
        )
    }
}