package com.example.havetime.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.time.LocalDate
import java.time.format.TextStyle

@Composable
fun getShortDayOfWeekName(date: LocalDate): String {
    val currentLocale = LocalConfiguration.current.locales[0]
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, currentLocale)
        .uppercase(currentLocale)
}