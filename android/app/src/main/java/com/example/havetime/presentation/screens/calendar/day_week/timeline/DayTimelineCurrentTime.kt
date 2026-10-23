package com.example.havetime.presentation.screens.calendar.day_week.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.time.LocalDateTime

@Composable
internal fun DayTimelineCurrentTime(currentDateTime: LocalDateTime, minuteHeightPx: Float) {
    val currentMinutes = currentDateTime.hour * 60 + currentDateTime.minute
    val yPos = currentMinutes * minuteHeightPx
    Box(
        modifier = Modifier.fillMaxWidth().graphicsLayer {
            translationY = yPos
        }.zIndex(600f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(
                thickness = 1.5.dp,
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
    }
}
