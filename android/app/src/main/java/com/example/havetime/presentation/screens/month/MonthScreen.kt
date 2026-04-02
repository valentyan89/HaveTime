package com.example.havetime.presentation.screens.month

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun MonthScreen(
    currentDate: Calendar,
    getIntensity: (Calendar) -> Int,
    onDayClick: (Calendar) -> Unit
) {
    // Подготовка данных месяца
    val monthCalendar = currentDate.clone() as Calendar
    monthCalendar.set(Calendar.DAY_OF_MONTH, 1)

    val monthName = SimpleDateFormat("LLLL yyyy", Locale.getDefault()).format(monthCalendar)
    val firstDayOfWeek = monthCalendar.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val offset = when (firstDayOfWeek) {
        Calendar.MONDAY -> 0
        Calendar.TUESDAY -> 1
        Calendar.WEDNESDAY -> 2
        Calendar.THURSDAY -> 3
        Calendar.FRIDAY -> 4
        Calendar.SATURDAY -> 5
        Calendar.SUNDAY -> 6
        else -> 0
    }

    Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        Text(
            text = monthName.replaceFirstChar { it.uppercase() },
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
        )

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            val weekDays = listOf("Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun")
            weekDays.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize()
        ) {
            items(offset) {
                Spacer(modifier = Modifier.padding(4.dp))
            }

            items(daysInMonth) { index ->
                val dayNum = index + 1
                val dayCal = monthCalendar.clone() as Calendar
                dayCal.set(Calendar.DAY_OF_MONTH, dayNum)

                val intensity = getIntensity(dayCal)

                val dayColor = when {
                    intensity > 480 -> Color(0xFFEF5350)
                    intensity > 120 -> Color(0xFFFFA726)
                    intensity > 0 -> Color(0xFF66BB6A)
                    else -> Color.Transparent
                }

                Card(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDayClick(dayCal) },
                    colors = CardDefaults.cardColors(
                        containerColor = dayColor.copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = dayNum.toString(),
                            fontSize = 16.sp,
                            fontWeight = if (intensity > 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (intensity > 0) dayColor else Color.Black
                        )
                    }
                }
            }
        }
    }
}