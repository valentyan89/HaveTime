package com.example.havetime.presentation.screens.calendar.month

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.core.CalendarMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthSectionHeader(
    monthData: CalendarMonth,
    currentLocale: Locale
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 12.dp, start = 8.dp)
    ) {
        val headerMonthName = remember(monthData, currentLocale) {
            val formatter = DateTimeFormatter.ofPattern("MMMM", currentLocale)
            monthData.yearMonth.format(formatter)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(currentLocale) else it.toString() }
        }

        Text(
            text = "$headerMonthName ${monthData.yearMonth.year}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp, end = 16.dp))
    }
}