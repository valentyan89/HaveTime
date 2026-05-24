package com.example.havetime.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

interface DateRepository {
    fun getCurrentTime(): Flow<LocalDateTime>
    fun getCurrentDate(): Flow<LocalDate>
    fun getCurrentYear(): Flow<Int>
}