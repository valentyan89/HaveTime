package com.example.calendar.domain.repository

import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.Activity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getTodos(): Flow<List<Activity>>
    fun getIntervalsForDate(date: LocalDate): Flow<List<Activity>>
    fun addTodo(todo: Activity): Flow<Unit>
    fun deleteTodo(id: Int): Flow<Unit>
    fun syncWithServer(): Flow<Unit>
    fun saveInterval(interval: TimeInterval): Flow<Unit>
}