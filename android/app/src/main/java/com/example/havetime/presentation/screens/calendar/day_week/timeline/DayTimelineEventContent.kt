package com.example.havetime.presentation.screens.calendar.day_week.timeline

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.common.utils.HaveText
import java.time.Instant
import java.time.ZoneId

@Composable
internal fun DayTimelineEventContent(
    event: Activity,
    currentStart: Long,
    currentEnd: Long
) {
    Column(modifier = Modifier.padding(8.dp)) {
        HaveText(
            event.title,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 14.sp,
            maxLines = 1
        )

        val sT = Instant.ofEpochMilli(
            currentStart
        ).atZone(ZoneId.systemDefault()).toLocalTime()
        val eT = Instant.ofEpochMilli(
            currentEnd
        ).atZone(ZoneId.systemDefault()).toLocalTime()
        HaveText(
            "%02d:%02d - %02d:%02d".format(
                sT.hour,
                sT.minute,
                eT.hour,
                eT.minute
            ),
            color = Color.White.copy(0.8f),
            fontSize = 11.sp
        )

        if (!event.location?.geocodedAddress.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(2.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Адрес",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(2.dp))

                event.location.geocodedAddress.let {
                    HaveText(
                        text = it,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp
                    )
                }
            }
        } else if (event.location != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Координаты",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(2.dp))

                val str = "${event.location.latitude}, ${event.location.longitude}"
                HaveText(
                    text = str,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
