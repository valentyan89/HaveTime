package com.example.havetime.presentation.screens.day

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.domain.model.TimeInterval

@Composable
fun DayScreen(intervals: List<TimeInterval>) {
    val hourHeight = 60.dp
    Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column {
            repeat(24) { hour ->
                Row(modifier = Modifier.height(hourHeight)) {
                    Text(String.format("%02d:00", hour), fontSize = 12.sp, modifier = Modifier.width(50.dp).padding(start = 8.dp))
                    HorizontalDivider(modifier = Modifier.padding(top = 8.dp), thickness = 0.5.dp)
                }
            }
        }
        intervals.forEach { interval ->
            val startMin = interval.start.get(Calendar.HOUR_OF_DAY) * 60 + interval.start.get(Calendar.MINUTE)
            val duration = ((interval.end.timeInMillis - interval.start.timeInMillis) / 60000).toInt()

            Box(modifier = Modifier
                .padding(start = 60.dp, end = 8.dp)
                .offset(y = startMin.dp)
                .height(duration.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Red.copy(alpha = 0.4f))
                .padding(4.dp)
            ) { Text(interval.title, fontSize = 10.sp) }
        }
    }
}