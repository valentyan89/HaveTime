package com.example.havetime.presentation.screens.month

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kizitonwose.calendar.compose.VerticalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthScreen(
    currentDate: LocalDate,
    getIntensity: (LocalDate) -> Int,
    onDayClick: (LocalDate) -> Unit
) {
    val currentMonth = remember { YearMonth.from(currentDate) }
    val startMonth = remember { currentMonth.minusMonths(24) }
    val endMonth = remember { YearMonth.of(2077, 12) }
    val daysOfWeek = remember { daysOfWeek(firstDayOfWeek = DayOfWeek.MONDAY) }
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            daysOfWeek.forEach { dayOfWeek ->
                Text(
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        VerticalCalendar(
            state = state,
            dayContent = { day ->
                val isToday = day.date == LocalDate.now()
                val intensity = if (day.position == DayPosition.MonthDate) getIntensity(day.date) else 0
                val backgroundColor = when {
                    intensity > 480 -> Color(0xFFEF5350).copy(alpha = 0.4f) // Высокая нагрузка
                    intensity > 120 -> Color(0xFFFFA726).copy(alpha = 0.3f) // Средняя
                    intensity > 0 -> Color(0xFF66BB6A).copy(alpha = 0.2f)   // Низкая
                    else -> Color.Transparent
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(if (isToday) Color.LightGray.copy(0.3f) else backgroundColor)
                        .clickable(enabled = day.position == DayPosition.MonthDate) {
                            onDayClick(day.date)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        fontSize = 14.sp,
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (day.position == DayPosition.MonthDate) Color.Black else Color.LightGray
                    )
                }
            },
            monthHeader = { month ->
                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 8.dp),
                    text = month.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        .replaceFirstChar { it.uppercase() } + " ${month.yearMonth.year}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}