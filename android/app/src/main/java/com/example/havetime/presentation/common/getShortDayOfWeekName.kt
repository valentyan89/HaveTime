package com.example.havetime.presentation.common

import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
fun getShortDayOfWeekName(date: LocalDate): String {
    val currentLocale = Locale.getDefault()
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, currentLocale)
        .uppercase(currentLocale)
}