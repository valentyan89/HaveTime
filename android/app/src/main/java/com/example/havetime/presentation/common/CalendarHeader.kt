package com.example.havetime.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarHeader(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onWeekClick: () -> Unit,
    onMonthClick: () -> Unit,
    onYearClick: () -> Unit,
    onDayClick: () -> Unit,
    currentDestination: String?
) {
    val today = LocalDate.now()
    val locale = Locale.getDefault()
    val handleNavigationClick = { targetRoute: String, action: () -> Unit ->
        if (currentDestination == targetRoute) {
            if (selectedDate != today) onDateSelected(today)
        } else {
            action()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { handleNavigationClick("day", onDayClick) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(82.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(selectedDate.dayOfMonth.toString())
        }
        Button(
            onClick = { handleNavigationClick("week", onWeekClick) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(120.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() })
        }
        Button(
            onClick = { handleNavigationClick("month", onMonthClick) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(120.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(selectedDate.month.getDisplayName(TextStyle.FULL, locale).replaceFirstChar { it.uppercase() })
        }
        Button(
            onClick = { handleNavigationClick("year", onYearClick) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(82.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(selectedDate.year.toString())
        }
    }
}