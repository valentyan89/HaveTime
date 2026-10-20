package com.example.havetime.data.repository

import com.example.havetime.domain.repository.DateRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

class DateRepositoryImpl @Inject constructor() : DateRepository {
    override fun getInitialDate(): LocalDate {
        return LocalDate.now()
    }

    override fun getInitialDateTime(): LocalDateTime = LocalDateTime.now()

    override fun getCurrentTime(): Flow<LocalDateTime> = flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(60 * 1000)
        }
    }

    override fun getCurrentDate(): Flow<LocalDate> = getCurrentTime().map {
        it.toLocalDate()
    }.distinctUntilChanged()

    override fun getCurrentYear(): Flow<Int> = getCurrentTime().map {
        it.toLocalDate().year
    }.distinctUntilChanged()

    override fun getCurrentTimeOnly(): Flow<LocalTime> = getCurrentTime().map {
        it.toLocalTime()
    }.distinctUntilChanged()

    override fun isToday(date: LocalDate): Flow<Boolean> = getCurrentDate().map {
        date == it
    }.distinctUntilChanged()

    override fun getMonday(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    override fun getFirstDayOfWeek(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    override fun getLastDayOfWeek(date: LocalDate): LocalDate =
        date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    override fun getFirstDayOfMonth(yearMonth: YearMonth): LocalDate = yearMonth.atDay(1)

    override fun getLastDayOfMonth(yearMonth: YearMonth): LocalDate = yearMonth.atEndOfMonth()

    override fun getFirstDayOfGrid(yearMonth: YearMonth): LocalDate {
        val firstDay = getFirstDayOfMonth(yearMonth)
        return getFirstDayOfWeek(firstDay)
    }

    override fun isSameWeek(date1: LocalDate, date2: LocalDate): Boolean =
        getMonday(date1) == getMonday(date2)

    override fun isSameMonth(date: LocalDate, yearMonth: YearMonth): Boolean =
        date.year == yearMonth.year && date.month == yearMonth.month

    override fun getNextDay(date: LocalDate): LocalDate = date.plusDays(1)

    override fun getPreviousDay(date: LocalDate): LocalDate = date.minusDays(1)

    override fun getNextWeek(date: LocalDate): LocalDate = date.plusWeeks(1)

    override fun getPreviousWeek(date: LocalDate): LocalDate = date.minusWeeks(1)

    override fun getNextMonth(yearMonth: YearMonth): YearMonth = yearMonth.plusMonths(1)

    override fun getPreviousMonth(yearMonth: YearMonth): YearMonth = yearMonth.minusMonths(1)

    override fun getNextYear(currentYear: Int): Int = currentYear + 1

    override fun getPreviousYear(currentYear: Int): Int = currentYear - 1
}
