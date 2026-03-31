package com.example.calendar.domain.repository

import com.example.havetime.domain.model.TimeInterval
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface CalendarRepository {
    fun getIntervalsForDate(date: LocalDate): Flow<List<TimeInterval>>
    suspend fun saveInterval(interval: TimeInterval)
}