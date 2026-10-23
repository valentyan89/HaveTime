package com.example.havetime.presentation.screens.map.components

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
internal fun MapWeekPager(date: LocalDate, today: LocalDate, onSelectDate: (LocalDate) -> Unit) {
    val pagerState = rememberLazyListState(initialFirstVisibleItemIndex = 5000)
    val snapFlingBehavior =
        rememberSnapFlingBehavior(lazyListState = pagerState)

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
        val weeksDiff = ChronoUnit.WEEKS.between(
            mondayOfToday,
            mondayOfDate
        ).toInt()
        val target = 5000 + weeksDiff
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
        items(10000) { weekIndex ->
            val startOfWeek =
                today
                    .with(
                        java.time.temporal.TemporalAdjusters
                            .previousOrSame(DayOfWeek.MONDAY)
                    ).plusWeeks((weekIndex - 5000).toLong())

            Row(modifier = Modifier.fillParentMaxWidth()) {
                (0..6).forEach { dayOffset ->
                    val itemDate = startOfWeek.plusDays(dayOffset.toLong())
                    MapWeekDayCell(itemDate, date, today, onSelectDate, Modifier.weight(1f))
                }
            }
        }
    }
}
