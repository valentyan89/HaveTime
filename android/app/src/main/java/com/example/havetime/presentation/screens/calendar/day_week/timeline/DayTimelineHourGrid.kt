package com.example.havetime.presentation.screens.calendar.day_week.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.presentation.common.utils.HaveText
import java.util.Locale

private const val HOUR_HEIGHT_DP = 90f
private const val TIME_COLUMN_WIDTH_DP = 60f

@Composable
internal fun DayTimelineHourGrid() {
    repeat(24) { hour ->
        val topOffsetDp = (hour * HOUR_HEIGHT_DP).dp
        Box(
            modifier =
            Modifier
                .fillMaxWidth()
                .height(HOUR_HEIGHT_DP.dp)
                .offset(y = topOffsetDp)
        ) {
            HaveText(
                text = "%02d:00".format(Locale.getDefault(), hour),
                modifier =
                Modifier
                    .width(TIME_COLUMN_WIDTH_DP.dp)
                    .align(Alignment.TopStart),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = TIME_COLUMN_WIDTH_DP.dp)
                    .align(Alignment.TopStart),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
        }
    }
}
