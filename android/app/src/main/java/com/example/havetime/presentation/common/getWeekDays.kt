package com.example.havetime.presentation.common

import java.time.DayOfWeek
import java.time.LocalDate

fun getWeekDays(centerDate: LocalDate): List<LocalDate> {
    var monday = centerDate
    while (monday.dayOfWeek != DayOfWeek.MONDAY) {
        monday = monday.minusDays(1)
    }
    return (0..6).map { monday.plusDays(it.toLong()) }
}