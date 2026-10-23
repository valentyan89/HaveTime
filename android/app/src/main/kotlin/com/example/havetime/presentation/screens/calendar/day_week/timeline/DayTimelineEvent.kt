package com.example.havetime.presentation.screens.calendar.day_week.timeline

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.havetime.domain.model.Activity
import java.time.LocalDate

private const val HOUR_HEIGHT_DP = 90f
private const val TIME_COLUMN_WIDTH_DP = 60f

@Composable
internal fun DayTimelineEvent(
    event: Activity,
    index: Int,
    dayVisibleIntervals: List<Activity>,
    selectedDate: LocalDate,
    dayStart: Long,
    dayEnd: Long,
    minuteHeightPx: Float,
    isActive: Boolean,
    onActiveChange: (Int?) -> Unit,
    onScrollBy: (Float) -> Unit,
    onEventClick: (Activity) -> Unit,
    onUpdateActivity: (Activity) -> Unit
) {
    val density = LocalDensity.current
    val dragState = remember(event.id) { TimelineEventDragState(event.timeInterval) }
    val latestEvent by rememberUpdatedState(event)
    val latestOnActiveChange by rememberUpdatedState(onActiveChange)
    val latestOnScrollBy by rememberUpdatedState(onScrollBy)
    val latestOnUpdateActivity by rememberUpdatedState(onUpdateActivity)

    LaunchedEffect(event.timeInterval) {
        dragState.syncInterval(event.timeInterval)
    }
    val currentStart = dragState.interval.startTime
    val currentEnd = dragState.interval.endTime
    val simultaneous = dayVisibleIntervals.filter {
        it.timeInterval.startTime ==
            event.timeInterval.startTime
    }
    val widthFactor: Float
    val horizontalOffsetFactor: Float
    if (simultaneous.size > 1) {
        widthFactor = 1f / simultaneous.size
        val pos = simultaneous.indexOf(event)
        horizontalOffsetFactor = pos.toFloat() / simultaneous.size
    } else {
        widthFactor = 1f
        horizontalOffsetFactor = 0f
    }
    val displayStart = if (currentStart < dayStart) dayStart else currentStart
    val displayEnd = if (currentEnd > dayEnd) dayEnd else currentEnd
    val startMin = (displayStart - dayStart) / 60000f
    val durMin = (displayEnd - displayStart) / 60000f
    if (durMin > 0 || dragState.isDragging) {
        val eventColor =
            lerp(Color(event.color), MaterialTheme.colorScheme.surface, 0.0f)
        Card(
            modifier =
            Modifier
                .graphicsLayer {
                    translationY = startMin * minuteHeightPx
                }
                .layout { measurable, constraints ->
                    val leftInset = TIME_COLUMN_WIDTH_DP.dp.roundToPx()
                    val gap = 4.dp.roundToPx()
                    val availableWidth =
                        (constraints.maxWidth - leftInset).coerceAtLeast(0)
                    val columnStart =
                        (availableWidth * horizontalOffsetFactor).toInt()
                    val columnEnd =
                        (availableWidth * (horizontalOffsetFactor + widthFactor)).toInt()
                    val cardWidth = (columnEnd - columnStart - gap * 2)
                        .coerceAtLeast(0)
                    val placeable = measurable.measure(
                        constraints.copy(minWidth = cardWidth, maxWidth = cardWidth)
                    )
                    layout(constraints.maxWidth, placeable.height) {
                        placeable.placeRelative(leftInset + columnStart + gap, 0)
                    }
                }
                .height(with(density) { (durMin.coerceAtLeast(1f) * minuteHeightPx).toDp() })
                .zIndex(if (isActive) 1000f else 10f + index)
                .pointerInput(event.id, selectedDate, minuteHeightPx) {
                    detectDragGesturesAfterLongPress(
                        onDragStart = {
                            dragState.start()
                            latestOnActiveChange(event.id)
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragState.dragBy(dragAmount.y, minuteHeightPx)
                            if (change.position.y < 50f) {
                                latestOnScrollBy(-30f)
                            } else if (change.position.y >
                                (24 * HOUR_HEIGHT_DP * density.density - 50f)
                            ) {
                                latestOnScrollBy(30f)
                            }
                        },
                        onDragEnd = {
                            dragState.finish()?.let { interval ->
                                val updatedEvent = latestEvent.copy(
                                    timeInterval = interval,
                                    lastTimeModified = System.currentTimeMillis()
                                )
                                latestOnUpdateActivity(updatedEvent)
                            }
                            latestOnActiveChange(null)
                        },
                        onDragCancel = {
                            dragState.cancel()
                            latestOnActiveChange(null)
                        }
                    )
                }.clickable { onEventClick(event) },
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = eventColor)
        ) {
            DayTimelineEventContent(event, currentStart, currentEnd)
        }
    }
}
