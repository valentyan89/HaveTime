package com.example.havetime.presentation.screens.calendar.day_week.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.time.LocalDate
import java.time.ZoneId

private const val TIME_COLUMN_WIDTH_DP = 60f

@Composable
internal fun DayTimelineCreationPreview(
    creatingActivityStart: Long?,
    creatingActivityEnd: Long?,
    selectedDate: LocalDate,
    minuteHeightPx: Float
) {
    val density = LocalDensity.current
    creatingActivityStart?.let { s ->
        creatingActivityEnd?.let { e ->
            val start = if (s < e) s else e
            val end = if (s < e) e else s
            val dayStart =
                selectedDate
                    .atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            val dayEnd =
                selectedDate
                    .plusDays(1)
                    .atStartOfDay()
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
            if (start < dayEnd && end > dayStart) {
                val drawStart = if (start < dayStart) dayStart else start
                val drawEnd = if (end > dayEnd) dayEnd else end
                val offMin = (drawStart - dayStart) / 60000f
                val durMin = (drawEnd - drawStart) / 60000f
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = TIME_COLUMN_WIDTH_DP.dp, end = 16.dp)
                        .graphicsLayer { translationY = offMin * minuteHeightPx }
                        .height(with(density) { (durMin * minuteHeightPx).toDp() })
                        .background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .zIndex(400f)
                )
            }
        }
    }
}
