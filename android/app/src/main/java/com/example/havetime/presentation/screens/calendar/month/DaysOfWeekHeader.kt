package com.example.havetime.presentation.screens.calendar.month

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlin.collections.forEach

@Composable
fun DaysOfWeekHeader(
    daysOfWeek: List<DayOfWeek>,
    currentLocale: Locale
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            val dayName = remember(dayOfWeek, currentLocale) {
                dayOfWeek.getDisplayName(TextStyle.SHORT, currentLocale)
            }
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayName,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

