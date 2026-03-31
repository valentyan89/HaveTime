package com.example.havetime.presentation.screens.week

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun WeekScreen(
    currentDate: Calendar,
    getBusyMinutes: (Calendar) -> Int,
    onDayClick: (Calendar) -> Unit
) {
    val weekStart = currentDate.clone() as Calendar
    weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(7) { index ->
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, index)

            val busyMinutes = getBusyMinutes(day)
            val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(day) // Полное название
            val dayNumber = day.get(Calendar.DAY_OF_MONTH).toString()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable { onDayClick(day) }
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Число и день недели
                    Text(text = dayNumber, fontSize = 24.sp, modifier = Modifier.width(40.dp))
                    Text(text = dayName, fontSize = 16.sp, color = Color.Gray, modifier = Modifier.weight(1f))

                    // Краткое сведение (полоска занятости)
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        val fillWidth = (busyMinutes.toFloat() / 1440f).coerceIn(0.05f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fillWidth)
                                .fillMaxHeight()
                                .background(if (busyMinutes > 400) Color.Red else Color.Green)
                        )
                    }
                    Text(
                        text = " ${busyMinutes / 60}ч",
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(top = 12.dp), thickness = 0.5.dp)
            }
        }
    }
}