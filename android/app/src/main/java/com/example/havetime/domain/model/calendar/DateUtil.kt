package com.example.havetime.domain.model.calendar

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

class DateUtil {
    private val weekFields = WeekFields.of(Locale.getDefault())
    private val dayFormatter = DateTimeFormatter.ofPattern("EE", Locale.getDefault())
    private val monthFormatter = DateTimeFormatter.ofPattern("LLLL", Locale.getDefault())

    fun getDayShortName(date: LocalDate): String{
        return date.format(dayFormatter).uppercase()
    }

    fun getMonthName(date: LocalDate): String{
        return date.format(monthFormatter).uppercase()
    }

    fun getStartWeek(date: LocalDate): LocalDate{
        return date.with(weekFields.dayOfWeek(), 1)
    }

    fun getEndWeek(date: LocalDate): LocalDate{
        return date.with(weekFields.dayOfWeek(), 7)
    }

    fun getDaysMonthGrid(date: LocalDate): List<LocalDate>{
        val firstDayOfMonth = YearMonth.from(date).atDay(1)
        val lastDayOfMonth = YearMonth.from(date).atEndOfMonth()

        val gridStart = getStartWeek(firstDayOfMonth)
        val gridEnd = getEndWeek(lastDayOfMonth)

        val days = mutableListOf<LocalDate>()
        var current = gridStart

        while (current <= gridEnd) {
            days.add(current)
            current = current.plusDays(1)
        }

        return days
    }
}