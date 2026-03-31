package com.example.havetime.presentation.common

import android.graphics.drawable.shapes.Shape
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun CalendarHeader(
    selectedDate: Calendar,
    onDayClick: () -> Unit,
    onMonthClick: () -> Unit,
    onYearClick: () -> Unit
) {
    val dayFormat = SimpleDateFormat("d", Locale.getDefault())
    val weekDayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    val monthFormat = SimpleDateFormat("LLLL", Locale.getDefault())
    val yearFormat = SimpleDateFormat("yyyy", Locale.getDefault())

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = onDayClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(82.dp)
        ) { Text(dayFormat.format(selectedDate)) }

        Button(onClick = onDayClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(120.dp)
        ) { Text(weekDayFormat.format(selectedDate)) }

        Button(onClick = onMonthClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(120.dp)
        ) { Text(monthFormat.format(selectedDate)) }

        Button(onClick = onYearClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.width(82.dp)
        ) { Text(yearFormat.format(selectedDate)) }
    }
}