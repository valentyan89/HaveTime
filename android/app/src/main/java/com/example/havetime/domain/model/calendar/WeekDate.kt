package com.example.havetime.domain.model.calendar

import java.time.LocalDate

data class WeekDate(
    val startWeek: LocalDate,
    val endWeek: LocalDate,
    val daysWeek: List<DayDate> = emptyList()
)