package com.example.calendar.domain.repository

import com.example.havetime.domain.model.Activity
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    fun getActivitiesForMonth(yearMonth: YearMonth): Flow<List<Activity>>

    fun getActivities(): Flow<List<Activity>>

    fun getIntervalsForDate(date: LocalDate): Flow<List<Activity>>

    fun addActivity(activity: Activity): Flow<Int>

    fun deleteActivity(id: Int): Flow<Unit>

    suspend fun syncWithServer(): Result<Unit>

    fun updateActivity(activity: Activity): Flow<Unit>

    fun searchActivities(query: String): Flow<List<Activity>>

    suspend fun getActivityById(id: Int): Activity?

    suspend fun getUpcomingActivities(limit: Int): List<Activity>
}
