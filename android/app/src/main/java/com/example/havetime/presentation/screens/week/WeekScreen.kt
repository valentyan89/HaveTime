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
import com.example.havetime.domain.model.TimeInterval
import java.util.Locale

@Composable
fun WeekScreen(
    currentDate: Calendar,
    getIntervals: (Calendar) -> List<TimeInterval>,
    onDayClick: (Calendar) -> Unit
) {
    val weekStart = currentDate.clone() as Calendar
    weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(7) { index ->
            val day = weekStart.clone() as Calendar
            day.add(Calendar.DAY_OF_MONTH, index)

            val dayIntervals = getIntervals(day)
            val dayOfMonth = day.get(Calendar.DAY_OF_MONTH).toString()
            val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(day)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clickable { onDayClick(day) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dayOfMonth, fontSize = 20.sp, modifier = Modifier.width(35.dp))
                    Text(text = dayName, fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray.copy(alpha = 0.2f))
                ) {
                    dayIntervals.forEach { interval ->
                        val startHour = interval.start.get(Calendar.HOUR_OF_DAY)
                        val startMin = interval.start.get(Calendar.MINUTE)
                        val startTotalMin = startHour * 60 + startMin

                        val diffMillis = interval.end.timeInMillis - interval.start.timeInMillis
                        val durationMin = (diffMillis / 60000).toInt()

                        val startBias = startTotalMin.toFloat() / 1440f
                        val widthRatio = durationMin.toFloat() / 1440f

                        Row(modifier = Modifier.fillMaxSize()) {
                            if (startBias > 0) {
                                Spacer(modifier = Modifier.fillMaxWidth(startBias))
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(widthRatio / (1f - startBias))
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(interval.color))
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Text(
                                    text = interval.title,
                                    fontSize = 9.sp,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}