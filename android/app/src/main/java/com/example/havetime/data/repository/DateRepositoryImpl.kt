package com.example.havetime.data.repository

import com.example.havetime.domain.repository.DateRepository
import com.kizitonwose.calendar.core.atStartOfMonth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class DateRepositoryImpl @Inject constructor() : DateRepository {
    override fun getCurrentTime(): Flow<LocalDateTime> = flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(60 * 1000)
        }
    }

    override fun getCurrentDate(): Flow<LocalDate> {
        return getCurrentTime().map { it.toLocalDate() }.distinctUntilChanged()
    }

    override fun getCurrentYear(): Flow<Int> {
        return getCurrentTime().map { it.toLocalDate().year }.distinctUntilChanged()
    }

    override fun getCurrentTimeOnly(): Flow<LocalTime> {
        return getCurrentTime().map { it.toLocalTime() }.distinctUntilChanged()
    }

    override fun isToday(date: LocalDate): Flow<Boolean> {
        return getCurrentDate().map { date == it }.distinctUntilChanged()
    }

    override fun getMonday(date: LocalDate): LocalDate {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    override fun getFirstDayOfWeek(date: LocalDate): LocalDate {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    override fun getLastDayOfWeek(date: LocalDate): LocalDate {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
    }

    override fun getFirstDayOfMonth(yearMonth: YearMonth): LocalDate {
        return yearMonth.atDay(1)
    }

    override fun getLastDayOfMonth(yearMonth: YearMonth): LocalDate {
        return yearMonth.atEndOfMonth()
    }

    override fun getFirstDayOfGrid(yearMonth: YearMonth): LocalDate {
        val firstDay = getFirstDayOfMonth(yearMonth)
        return getFirstDayOfWeek(firstDay)
    }

    override fun isSameWeek(date1: LocalDate, date2: LocalDate): Boolean {
        return getMonday(date1) == getMonday(date2)
    }

    override fun isSameMonth(date: LocalDate, yearMonth: YearMonth): Boolean {
        return date.year == yearMonth.year && date.month == yearMonth.month
    }

    override fun getNextDay(date: LocalDate): LocalDate {
        return date.plusDays(1)
    }

    override fun getPreviousDay(date: LocalDate): LocalDate {
        return date.minusDays(1)
    }

    override fun getNextWeek(date: LocalDate): LocalDate {
        return date.plusWeeks(1)
    }

    override fun getPreviousWeek(date: LocalDate): LocalDate {
        return date.minusWeeks(1)
    }

    override fun getNextMonth(yearMonth: YearMonth): YearMonth {
        return yearMonth.plusMonths(1)
    }

    override fun getPreviousMonth(yearMonth: YearMonth): YearMonth {
        return yearMonth.minusMonths(1)
    }

    override suspend fun getNextYear(currentYear: Int): Int {
        return currentYear + 1
    }

    override suspend fun getPreviousYear(currentYear: Int): Int {
        return currentYear - 1
    }
}