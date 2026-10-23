package com.example.havetime.presentation.screens.calendar.day_week.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Locale

private const val INFINITE_PAGER_ITEMS = 10_000
private const val PAGER_START_INDEX = INFINITE_PAGER_ITEMS / 2

@Composable
fun WeekCalendarPager(
    date: LocalDate,
    today: LocalDate,
    currentLocale: Locale,
    onSelectDate: (LocalDate) -> Unit
) {
    val pagerState = rememberLazyListState(initialFirstVisibleItemIndex = PAGER_START_INDEX)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = pagerState)

    LaunchedEffect(date) {
        val mondayOfDate =
            date.with(
                java.time.temporal.TemporalAdjusters
                    .previousOrSame(DayOfWeek.MONDAY)
            )
        val mondayOfToday =
            today.with(
                java.time.temporal.TemporalAdjusters
                    .previousOrSame(DayOfWeek.MONDAY)
            )
        val weeksDiff = ChronoUnit.WEEKS.between(mondayOfToday, mondayOfDate).toInt()
        val target = PAGER_START_INDEX + weeksDiff
        if (pagerState.firstVisibleItemIndex != target) {
            pagerState.animateScrollToItem(target)
        }
    }

    LazyRow(
        state = pagerState,
        flingBehavior = snapFlingBehavior,
        modifier =
        Modifier
            .fillMaxWidth()
            .height(70.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(INFINITE_PAGER_ITEMS) { weekIndex ->
            val startOfWeek =
                today
                    .with(
                        java.time.temporal.TemporalAdjusters
                            .previousOrSame(DayOfWeek.MONDAY)
                    ).plusWeeks((weekIndex - PAGER_START_INDEX).toLong())

            Row(modifier = Modifier.fillParentMaxWidth()) {
                (0..6).forEach { dayOffset ->
                    val itemDate = startOfWeek.plusDays(dayOffset.toLong())
                    WeekDayCell(itemDate, date, today, currentLocale, onSelectDate, Modifier.weight(1f))
                }
            }
        }
    }
}
