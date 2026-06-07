package com.example.havetime.presentation.screens.calendar.year

import java.time.Month
import java.time.YearMonth

sealed class YearState {
    object Loading : YearState()
    data class Success(
        val currentYear: Int,
        val monthsData: List<YearMonthData>
    ) : YearState()
    data class Error(val message: String) : YearState()
}

data class YearMonthData(
    val yearMonth: YearMonth,
    val month: Month,
    val daysInMonth: Int,
    val daysWithActivities: Int,
    val completionPercentage: Float
)