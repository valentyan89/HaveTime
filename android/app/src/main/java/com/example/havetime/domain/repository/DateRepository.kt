package com.example.havetime.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

interface DateRepository {
    fun getInitialDate(): LocalDate
    fun getInitialDateTime(): LocalDateTime
    fun getCurrentTime(): Flow<LocalDateTime>
    fun getCurrentDate(): Flow<LocalDate>
    fun getCurrentYear(): Flow<Int>
    fun getCurrentTimeOnly(): Flow<LocalTime>
    fun isToday(date: LocalDate): Flow<Boolean>
    fun getMonday(date: LocalDate): LocalDate
    fun getFirstDayOfWeek(date: LocalDate): LocalDate
    fun getLastDayOfWeek(date: LocalDate): LocalDate
    fun getFirstDayOfMonth(yearMonth: YearMonth): LocalDate
    fun getLastDayOfMonth(yearMonth: YearMonth): LocalDate
    fun getFirstDayOfGrid(yearMonth: YearMonth): LocalDate

    fun isSameWeek(date1: LocalDate, date2: LocalDate): Boolean
    fun isSameMonth(date: LocalDate, yearMonth: YearMonth): Boolean

    fun getNextDay(date: LocalDate): LocalDate
    fun getPreviousDay(date: LocalDate): LocalDate
    fun getNextWeek(date: LocalDate): LocalDate
    fun getPreviousWeek(date: LocalDate): LocalDate
    fun getNextMonth(yearMonth: YearMonth): YearMonth
    fun getPreviousMonth(yearMonth: YearMonth): YearMonth
    suspend fun getNextYear(currentYear: Int): Int
    suspend fun getPreviousYear(currentYear: Int): Int
}