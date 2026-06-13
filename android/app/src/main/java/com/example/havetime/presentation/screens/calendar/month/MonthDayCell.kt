package com.example.havetime.presentation.screens.calendar.month

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import java.time.LocalDate

@Composable
fun MonthDayCell(
    day: CalendarDay,
    today: LocalDate,
    intensityMap: Map<LocalDate, Int>,
    onDayClick: (LocalDate) -> Unit
) {
    val intensityHigh = colorResource(R.color.intensity_high)
    val intensityMedium = colorResource(R.color.intensity_medium)
    val intensityLow = colorResource(R.color.intensity_low)

    val isToday = day.date == today
    val isCurrentMonth = day.position == DayPosition.MonthDate

    val intensity = if (isCurrentMonth) intensityMap[day.date] ?: 0 else 0

    val backgroundColor = when {
        !isCurrentMonth -> Color.Transparent
        intensity >= 8 -> intensityHigh.copy(alpha = 0.4f)
        intensity >= 4 -> intensityMedium.copy(alpha = 0.3f)
        intensity > 0 -> intensityLow.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    val textColor = when {
        !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        isToday -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(
                if (isToday && isCurrentMonth) {
                    MaterialTheme.colorScheme.primary
                } else {
                    backgroundColor
                }
            )
            .clickable(enabled = true) {
                onDayClick(day.date)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            fontSize = 15.sp,
            fontWeight = if (isToday && isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}