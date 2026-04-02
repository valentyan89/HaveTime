package com.example.havetime.presentation.screens.year

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun YearScreen(
    currentDate: Calendar,
    onMonthClick: (Calendar) -> Unit
) {
    val startYear = currentDate.get(Calendar.YEAR)
    val maxYear = 2077

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(maxYear - startYear + 1) { yearOffset ->
            val year = startYear + yearOffset

            Text(
                text = year.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                for (row in 0..3) { // 4 ряда по 3 месяца
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..2) {
                            val monthIndex = row * 3 + col
                            val monthCal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, year)
                                set(Calendar.MONTH, monthIndex)
                                set(Calendar.DAY_OF_MONTH, 1)
                            }
                            val monthName = SimpleDateFormat("MMM", Locale.getDefault()).format(monthCal)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                                    .clickable { onMonthClick(monthCal) }
                            ) {
                                Text(text = monthName, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}