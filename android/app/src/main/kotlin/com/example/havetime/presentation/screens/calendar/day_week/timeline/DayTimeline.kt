package com.example.havetime.presentation.screens.calendar.day_week.timeline

import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.havetime.domain.model.Activity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.abs
import kotlinx.coroutines.launch

private const val HOUR_HEIGHT_DP = 90f

@Composable
fun DayTimeline(
    modifier: Modifier = Modifier,
    events: List<Activity>,
    currentDateTime: LocalDateTime,
    selectedDate: LocalDate,
    onEventClick: (Activity) -> Unit = {},
    onAddActivity: (Long, Long) -> Unit = { _, _ -> },
    onUpdateActivity: (Activity) -> Unit = {}
) {
    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val isTodaySelected = selectedDate == currentDateTime.toLocalDate()

    val hourHeightPx = with(density) { HOUR_HEIGHT_DP.dp.toPx() }
    val minuteHeightPx = hourHeightPx / 60f

    var creatingActivityStart by remember { mutableStateOf<Long?>(null) }
    var creatingActivityEnd by remember { mutableStateOf<Long?>(null) }

    var activeId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(selectedDate) {
        if (isTodaySelected) {
            val currentHour = currentDateTime.hour
            val targetHour = (currentHour - 2).coerceAtLeast(0)
            val targetOffsetPx = targetHour * hourHeightPx
            lazyListState.scrollToItem(0, targetOffsetPx.toInt())
        }
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier
    ) {
        item {
            val totalHeightDp = (24 * HOUR_HEIGHT_DP).dp

            Box(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(totalHeightDp)
                    .pointerInput(selectedDate) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                val totalMin = (offset.y / minuteHeightPx).toLong()
                                val startDT = selectedDate.atStartOfDay().plusMinutes(totalMin)
                                creatingActivityStart =
                                    startDT.atZone(
                                        ZoneId.systemDefault()
                                    ).toInstant().toEpochMilli()
                                creatingActivityEnd = creatingActivityStart
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (change.position.y < 50f) {
                                    scope.launch { lazyListState.animateScrollBy(-30f) }
                                } else if (change.position.y >
                                    (24 * HOUR_HEIGHT_DP * density.density - 50f)
                                ) {
                                    scope.launch { lazyListState.animateScrollBy(30f) }
                                }

                                val dMin = (dragAmount.y / minuteHeightPx).toLong()
                                creatingActivityEnd = (creatingActivityEnd ?: 0L) + dMin * 60000
                            },
                            onDragEnd = {
                                if (creatingActivityStart != null &&
                                    creatingActivityEnd != null
                                ) {
                                    val s = creatingActivityStart!!
                                    val e = creatingActivityEnd!!
                                    val start = if (s < e) s else e
                                    var end = if (s < e) e else s
                                    if (abs(end - start) < 10 * 60000) end = start + 10 * 60000
                                    onAddActivity(start, end)
                                }
                                creatingActivityStart = null
                                creatingActivityEnd = null
                            },
                            onDragCancel = {
                                creatingActivityStart = null
                                creatingActivityEnd = null
                            }
                        )
                    }
            ) {
                DayTimelineHourGrid()
                DayTimelineCreationPreview(
                    creatingActivityStart = creatingActivityStart,
                    creatingActivityEnd = creatingActivityEnd,
                    selectedDate = selectedDate,
                    minuteHeightPx = minuteHeightPx
                )

                val dayStart =
                    selectedDate
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                val dayEnd =
                    selectedDate
                        .plusDays(1)
                        .atStartOfDay()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()
                val dayVisibleIntervals =
                    events
                        .filter {
                            it.timeInterval.startTime < dayEnd && it.timeInterval.endTime > dayStart
                        }.sortedBy { it.timeInterval.startTime }

                dayVisibleIntervals.forEachIndexed { index, event ->
                    key(event.id) {
                        DayTimelineEvent(
                            event = event,
                            index = index,
                            dayVisibleIntervals = dayVisibleIntervals,
                            selectedDate = selectedDate,
                            dayStart = dayStart,
                            dayEnd = dayEnd,
                            minuteHeightPx = minuteHeightPx,
                            isActive = activeId == event.id,
                            onActiveChange = { activeId = it },
                            onScrollBy = { amount ->
                                scope.launch { lazyListState.animateScrollBy(amount) }
                            },
                            onEventClick = onEventClick,
                            onUpdateActivity = onUpdateActivity
                        )
                    }
                }

                DayTimelineCurrentTime(currentDateTime, minuteHeightPx)
            }
        }
    }
}
