package com.example.havetime.presentation.screens.calendar.year.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.havetime.presentation.common.utils.HaveText
import com.example.havetime.presentation.screens.calendar.year.YearMonthData
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthItem(
    monthData: YearMonthData,
    isCurrentMonth: Boolean,
    onClick: () -> Unit,
    locale: Locale
) {
    val monthName = monthData.month.getDisplayName(TextStyle.FULL, locale).replaceFirstChar {
        it.uppercase()
    }

    Column(
        modifier =
        Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HaveText(
            text = monthName,
            style = MaterialTheme.typography.labelLarge,
            color = if (isCurrentMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier =
            Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(
                    if (monthData.daysWithActivities > 0) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ).border(
                    width = if (isCurrentMonth) 2.dp else 0.dp,
                    color = if (isCurrentMonth) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (monthData.daysWithActivities > 0) {
                HaveText(
                    text = monthData.daysWithActivities.toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
