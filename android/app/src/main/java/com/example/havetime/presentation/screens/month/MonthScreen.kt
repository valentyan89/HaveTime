package com.example.havetime.presentation.screens.month

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MonthScreen(
    currentDate: Calendar,
    getIntensity: (Calendar) -> Int,
    onDayClick: (Calendar) -> Unit
) {
    val monthCalendar = currentDate.clone() as Calendar
    monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

    val firstDayOfWeek = monthCalendar.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Сдвиг для сетки (чтобы месяц начинался с нужного дня недели)
    val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        // Пустые ячейки до начала месяца
        items(offset) { Spacer(modifier = Modifier.padding(4.dp)) }

        // Дни месяца
        items(daysInMonth) { dayIndex ->
            val day = monthCalendar.clone() as Calendar
            day.set(Calendar.DAY_OF_MONTH, dayIndex + 1)

            val intensity = getIntensity(day)

            // Цвет в зависимости от занятости
            val bgColor = when {
                intensity > 80 -> Color.Red.copy(alpha = 0.6f)
                intensity > 40 -> Color.Yellow.copy(alpha = 0.6f)
                else -> Color.Green.copy(alpha = 0.2f)
            }

            Card(
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(2.dp)
                    .clickable { onDayClick(day) },
                colors = CardDefaults.cardColors(containerColor = bgColor)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = (dayIndex + 1).toString(), fontSize = 16.sp)
                }
            }
        }
    }
}