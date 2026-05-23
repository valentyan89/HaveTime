package com.example.havetime.domain.model.calendar

import java.time.YearMonth

data class MonthDate(
    val yearMonth: YearMonth,
    val weeksMonth: List<WeekDate> = emptyList(),
    val thisWeek: WeekDate? = null
)