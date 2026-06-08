package com.example.havetime.domain.model.calendar

data class YearDate(
    val year: Int,
    val monthsYear: List<MonthDate> = emptyList()
)