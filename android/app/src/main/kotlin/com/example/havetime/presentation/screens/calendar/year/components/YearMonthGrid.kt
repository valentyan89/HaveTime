package com.example.havetime.presentation.screens.calendar.year.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.havetime.presentation.screens.calendar.year.YearMonthData
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
internal fun YearMonthGrid(
    monthsData: List<YearMonthData>,
    currentYear: Int,
    today: LocalDate,
    currentLocale: Locale,
    onMonthClick: (YearMonth) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize().padding(8.dp),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(monthsData) { monthData ->
            val isCurrentMonth =
                monthData.month == today.month &&
                    currentYear == today.year

            MonthItem(
                monthData = monthData,
                isCurrentMonth = isCurrentMonth,
                onClick = {
                    onMonthClick(monthData.yearMonth)
                },
                locale = currentLocale
            )
        }
    }
}
