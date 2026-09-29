package com.example.havetime.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale

@Composable
fun CalendarHeader(
    selectedDate: LocalDate,
    todayDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onTodayClick: () -> Unit,
    onMonthClick: () -> Unit,
    currentDestination: String?
) {
    val locale = Locale.getDefault()
    val monthYearText = remember(selectedDate) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)
        selectedDate.format(formatter).replaceFirstChar { it.uppercase() }
    }

    val initialIndex = 500
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapBehavior = rememberSnapFlingBehavior(listState)

    LaunchedEffect(selectedDate) {
        val startOfSelectedWeek = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val startOfCurrentWeek = todayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weeksDiff = ChronoUnit.WEEKS.between(startOfCurrentWeek, startOfSelectedWeek).toInt()
        val targetIndex = initialIndex + weeksDiff
        
        if (listState.firstVisibleItemIndex != targetIndex) {
            listState.animateScrollToItem(targetIndex)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val weekOffset = listState.firstVisibleItemIndex - initialIndex
            val startOfCurrentWeek = todayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            val targetWeekStart = startOfCurrentWeek.plusWeeks(weekOffset.toLong())
            
            val weekEnd = targetWeekStart.plusDays(6)
            if (selectedDate.isBefore(targetWeekStart) || selectedDate.isAfter(weekEnd)) {
                onDateSelected(targetWeekStart)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00796B).copy(alpha = 0.1f))
                        .clickable { onTodayClick() }
                ) {
                    Text(
                        text = todayDate.dayOfMonth.toString(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00796B)
                    )
                }
                
                Spacer(Modifier.width(16.dp))

                Text(
                    text = monthYearText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D6A57),
                    modifier = Modifier.clickable { onMonthClick() }
                )
            }

            LazyRow(
                state = listState,
                flingBehavior = snapBehavior,
                modifier = Modifier.fillMaxWidth().height(70.dp)
            ) {
                items(1000) { index ->
                    val weekOffset = index - initialIndex
                    val startOfCurrentWeek = todayDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    val weekStart = startOfCurrentWeek.plusWeeks(weekOffset.toLong())
                    
                    Row(
                        modifier = Modifier.fillParentMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        (0..6).forEach { dayIdx ->
                            val date = weekStart.plusDays(dayIdx.toLong())
                            val isSelected = date == selectedDate
                            val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).uppercase()

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onDateSelected(date) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = dayName,
                                    fontSize = 11.sp,
                                    color = if (isSelected) Color(0xFF00796B) else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(Modifier.height(4.dp))
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) Color(0xFF00796B) else Color.Transparent)
                                ) {
                                    Text(
                                        text = date.dayOfMonth.toString(),
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color.White else Color.Black,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
