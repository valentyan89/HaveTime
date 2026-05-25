package com.example.havetime

import android.app.Application
import androidx.room.Room
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.local.ActivityDataBase
import com.example.havetime.data.repository.ActivityRepositoryImpl
import org.osmdroid.config.Configuration

class HaveTimeApplication : Application() {

    lateinit var activityRepository: ActivityRepository

    override fun onCreate() {
        super.onCreate()

        // Инициализация конфигурации Osmdroid
        Configuration.getInstance().userAgentValue = packageName
        Configuration.getInstance().load(this, android.preference.PreferenceManager.getDefaultSharedPreferences(this))

        val database = ActivityDataBase.getDatabase(this)

        activityRepository = ActivityRepositoryImpl(
            activityDao = database.activityDao()
        )
    }
}