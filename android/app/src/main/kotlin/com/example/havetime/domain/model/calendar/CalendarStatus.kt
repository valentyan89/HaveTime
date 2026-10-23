package com.example.havetime.domain.model.calendar

import java.time.LocalDate

data class CalendarStatus(
    val date: LocalDate,
    val calendarViewMode: CalendarViewMode = CalendarViewMode.DAY
)
