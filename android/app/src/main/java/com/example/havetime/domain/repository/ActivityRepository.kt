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
    suspend fun syncWithServer(): Result<Unit>
    fun updateActivity(activity: Activity): Flow<Unit>
    fun searchActivities(query: String): Flow<List<Activity>>
}