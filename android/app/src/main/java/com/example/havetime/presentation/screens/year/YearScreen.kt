package com.example.havetime.presentation.screens.year

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.VerticalYearCalendar
import com.kizitonwose.calendar.compose.yearcalendar.rememberYearCalendarState
import java.time.LocalDate
import java.time.Year
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun YearScreen(
    currentDate: LocalDate,
    onMonthClick: (LocalDate) -> Unit
) {
    val startYear = Year.now().minusYears(2)
    val endYear = Year.of(2077)

    val state = rememberYearCalendarState(
        startYear = startYear,
        endYear = endYear,
        firstVisibleYear = Year.from(currentDate)
    )

    VerticalYearCalendar(
        state = state,
        modifier = Modifier.fillMaxSize(),
        dayContent = { day ->
            Box(Modifier.size(2.dp))
        },
        monthHeader = { month ->
            Text(
                text = month.yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(4.dp)
                    .clickable {
                        onMonthClick(month.yearMonth.atDay(1))
                    }
            )
        },
        yearHeader = { year ->
            Text(
                text = year.year.toString(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    )
}