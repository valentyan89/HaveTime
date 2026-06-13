package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
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
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private const val INFINITE_PAGER_ITEMS = 10_000
private const val PAGER_START_INDEX = INFINITE_PAGER_ITEMS / 2

@Composable
fun WeekCalendarPager(
    date: LocalDate,
    today: LocalDate,
    currentLocale: Locale,
    onSelectDate: (LocalDate) -> Unit,
) {
    val pagerState = rememberLazyListState(initialFirstVisibleItemIndex = PAGER_START_INDEX)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = pagerState)

    LaunchedEffect(date) {
        val mondayOfDate = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val mondayOfToday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val weeksDiff = ChronoUnit.WEEKS.between(mondayOfToday, mondayOfDate).toInt()
        val target = PAGER_START_INDEX + weeksDiff
        if (pagerState.firstVisibleItemIndex != target) {
            pagerState.animateScrollToItem(target)
        }
    }

    LazyRow(
        state = pagerState,
        flingBehavior = snapFlingBehavior,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(INFINITE_PAGER_ITEMS) { weekIndex ->
            val startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .plusWeeks((weekIndex - PAGER_START_INDEX).toLong())

            Row(modifier = Modifier.fillParentMaxWidth()) {
                (0..6).forEach { dayOffset ->
                    val itemDate = startOfWeek.plusDays(dayOffset.toLong())
                    val isSelected = itemDate == date
                    val isTodayItem = itemDate == today
                    val dayOfWeekName = remember(itemDate, currentLocale) {
                        itemDate.dayOfWeek.getDisplayName(TextStyle.SHORT, currentLocale).uppercase()
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectDate(itemDate) }
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = dayOfWeekName,
                            fontSize = 12.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isSelected -> MaterialTheme.colorScheme.primary
                                        isTodayItem -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else -> Color.Transparent
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = itemDate.dayOfMonth.toString(),
                                fontSize = 16.sp,
                                fontWeight = if (isSelected || isTodayItem) FontWeight.Bold else FontWeight.Normal,
                                color = when {
                                    isSelected -> Color.White
                                    isTodayItem -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}