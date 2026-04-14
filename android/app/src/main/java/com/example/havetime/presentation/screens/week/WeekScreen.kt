package com.example.havetime.presentation.screens.week

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.domain.model.TimeInterval
import com.kizitonwose.calendar.compose.WeekCalendar
import com.kizitonwose.calendar.compose.weekcalendar.rememberWeekCalendarState
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

@Composable
fun WeekScreen(
    currentDate: LocalDate,
    getIntervals: (LocalDate) -> List<TimeInterval>,
    onDayClick: (LocalDate) -> Unit
) {
    val state = rememberWeekCalendarState(
        startDate = currentDate.minusWeeks(50),
        endDate = LocalDate.of(2077, 12, 31),
        firstVisibleWeekDate = currentDate
    )

    Column(modifier = Modifier.fillMaxSize()) {
        WeekCalendar(
            state = state,
            dayContent = { day ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clickable { onDayClick(day.date) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.date.dayOfMonth.toString(),
                        color = if (day.date == currentDate) Color(0xFF664FA3) else Color.Black,
                        fontWeight = if (day.date == currentDate) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        )

        HorizontalDivider(thickness = 0.5.dp)

        val daysInWeek = remember(state.firstVisibleWeek) {
            state.firstVisibleWeek.days.map { it.date }
        }

        LazyColumn(modifier = Modifier.fillWeight(1f)) {
            items(daysInWeek) { day ->
                val dayIntervals = getIntervals(day)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clickable { onDayClick(day) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(day.dayOfMonth.toString(), fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(35.dp))
                        Text(day.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault()), fontSize = 13.sp, color = Color.Gray)
                    }

                    Spacer(Modifier.height(8.dp))

                    // Полоска дня
                    Box(Modifier.fillMaxWidth().height(20.dp).clip(RoundedCornerShape(10.dp)).background(Color.LightGray.copy(0.2f))) {
                        dayIntervals.forEach { interval ->
                            val startMin = interval.start.hour * 60 + interval.start.minute
                            val duration = ChronoUnit.MINUTES.between(interval.start, interval.end).toInt()

                            val startBias = startMin / 1440f
                            val widthRatio = duration / 1440f

                            Row(Modifier.fillMaxSize()) {
                                if (startBias > 0) Spacer(Modifier.fillMaxWidth(startBias))
                                Box(
                                    Modifier.fillMaxHeight()
                                        .fillMaxWidth(if(startBias < 1f) widthRatio/(1f-startBias) else 1f)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(interval.color))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun Modifier.fillWeight(weight: Float): Modifier = this.then(Modifier)