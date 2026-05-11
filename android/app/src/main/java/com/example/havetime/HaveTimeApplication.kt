package com.example.havetime

import android.app.Application
import androidx.room.Room
import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.data.local.TodoDataBase
import com.example.havetime.data.repository.TodoRepositoryImpl

class HaveTimeApplication : Application() {

    lateinit var todoRepository: TodoRepository

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            this,
            TodoDataBase::class.java,
            "havetime_database"
        ).build()

        todoRepository = TodoRepositoryImpl(
            todoDao = database.todoDao()
        )
    }
}