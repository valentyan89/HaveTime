package com.example.havetime.presentation.common.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
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
internal fun CalendarWeekDayCell(date: LocalDate, selectedDate: LocalDate, locale: Locale, onDateSelected: (LocalDate) -> Unit, modifier: Modifier = Modifier) {
    val isSelected = date == selectedDate
    val dayName = date.dayOfWeek.getDisplayName(
        TextStyle.SHORT,
        locale
    ).uppercase()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
        modifier
            .clickable { onDateSelected(date) }
            .padding(vertical = 4.dp)
    ) {
        HaveText(
            text = dayName,
            fontSize = 11.sp,
            color = if (isSelected) Color(0xFF00796B) else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
        Spacer(Modifier.height(4.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier =
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        Color(
                            0xFF00796B
                        )
                    } else {
                        Color.Transparent
                    }
                )
        ) {
            HaveText(
                text = date.dayOfMonth.toString(),
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
