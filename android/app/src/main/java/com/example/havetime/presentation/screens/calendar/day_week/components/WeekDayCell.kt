package com.example.havetime.presentation.screens.calendar.day_week.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.presentation.common.utils.HaveText
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
internal fun WeekDayCell(itemDate: LocalDate, date: LocalDate, today: LocalDate, currentLocale: Locale, onSelectDate: (LocalDate) -> Unit, modifier: Modifier = Modifier) {
    val isSelected = itemDate == date
    val isTodayItem = itemDate == today
    val dayOfWeekName =
        remember(itemDate, currentLocale) {
            itemDate.dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                currentLocale
            ).uppercase()
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        modifier
            .padding(vertical = 4.dp)
    ) {
        HaveText(
            text = dayOfWeekName,
            fontSize = 12.sp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier =
            Modifier
                .size(38.dp)
                .clip(CircleShape)
                .clickable { onSelectDate(itemDate) }
                .background(
                    when {
                        isSelected -> MaterialTheme.colorScheme.primary
                        isTodayItem -> MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.15f
                        )
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            HaveText(
                text = itemDate.dayOfMonth.toString(),
                fontSize = 16.sp,
                fontWeight = if (isSelected ||
                    isTodayItem
                ) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
                color =
                when {
                    isSelected -> Color.White
                    isTodayItem -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
