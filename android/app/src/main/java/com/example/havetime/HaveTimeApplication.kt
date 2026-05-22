package com.example.havetime

import android.app.Application
import androidx.room.Room
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.local.ActivityDataBase
import com.example.havetime.data.repository.ActivityRepositoryImpl

class HaveTimeApplication : Application() {

    lateinit var todoRepository: ActivityRepository

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            this,
            ActivityDataBase::class.java,
            "havetime_database"
        ).build()

        todoRepository = ActivityRepositoryImpl(
            todoDao = database.todoDao()
        )
    }
}