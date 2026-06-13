package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.TimeInterval
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import kotlin.math.abs

private const val HOUR_HEIGHT_DP = 90f
private const val TIME_COLUMN_WIDTH_DP = 60f

@Composable
fun DayTimeline(
    events: List<Activity>,
    currentDateTime: LocalDateTime,
    selectedDate: LocalDate,
    onEventClick: (Activity) -> Unit = {},
    onAddActivity: (Long, Long) -> Unit = { _, _ -> },
    onUpdateActivity: (Activity) -> Unit = {},
    modifier: Modifier = Modifier
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(totalHeightDp)
                    .pointerInput(selectedDate) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                val totalMin = (offset.y / minuteHeightPx).toLong()
                                val startDT = selectedDate.atStartOfDay().plusMinutes(totalMin)
                                creatingActivityStart = startDT.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                creatingActivityEnd = creatingActivityStart
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (change.position.y < 50f) {
                                    scope.launch { lazyListState.animateScrollBy(-30f) }
                                } else if (change.position.y > (24 * HOUR_HEIGHT_DP * density.density - 50f)) {
                                    scope.launch { lazyListState.animateScrollBy(30f) }
                                }

                                val dMin = (dragAmount.y / minuteHeightPx).toLong()
                                creatingActivityEnd = (creatingActivityEnd ?: 0L) + dMin * 60000
                            },
                            onDragEnd = {
                                if (creatingActivityStart != null && creatingActivityEnd != null) {
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
                repeat(24) { hour ->
                    val topOffsetDp = (hour * HOUR_HEIGHT_DP).dp
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HOUR_HEIGHT_DP.dp)
                            .offset(y = topOffsetDp)
                    ) {
                        Text(
                            text = String.format(Locale("ru"), "%02d:00", hour),
                            modifier = Modifier
                                .width(TIME_COLUMN_WIDTH_DP.dp)
                                .align(Alignment.TopStart),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = TIME_COLUMN_WIDTH_DP.dp)
                                .align(Alignment.TopStart),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                        )
                    }
                }

                creatingActivityStart?.let { s -> creatingActivityEnd?.let { e ->
                    val start = if (s < e) s else e
                    val end = if (s < e) e else s
                    val dayStart = selectedDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    val dayEnd = selectedDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    if (start < dayEnd && end > dayStart) {
                        val drawStart = if (start < dayStart) dayStart else start
                        val drawEnd = if (end > dayEnd) dayEnd else end
                        val offMin = (drawStart - dayStart) / 60000f
                        val durMin = (drawEnd - drawStart) / 60000f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = TIME_COLUMN_WIDTH_DP.dp, end = 16.dp)
                                .graphicsLayer { translationY = offMin * minuteHeightPx }
                                .height(with(density) { (durMin * minuteHeightPx).toDp() })
                                .background(Color.Green.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .border(2.dp, Color.Green, RoundedCornerShape(8.dp))
                                .zIndex(400f)
                        )
                    }
                } }

                val dayStart = selectedDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val dayEnd = selectedDate.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val dayVisibleIntervals = events.filter {
                    it.timeInterval.startTime < dayEnd && it.timeInterval.endTime > dayStart
                }.sortedBy { it.timeInterval.startTime }

                dayVisibleIntervals.forEachIndexed { idx, event ->
                    var currentStart by remember(event.id, event.timeInterval.startTime) { mutableStateOf(event.timeInterval.startTime) }
                    var currentEnd by remember(event.id, event.timeInterval.endTime) { mutableStateOf(event.timeInterval.endTime) }
                    val overlapping = dayVisibleIntervals.filter {
                        it.timeInterval.startTime < event.timeInterval.endTime &&
                                it.timeInterval.endTime > event.timeInterval.startTime
                    }
                    val simultaneous = overlapping.filter { it.timeInterval.startTime == event.timeInterval.startTime }
                    val widthFactor: Float
                    val horizontalOffsetFactor: Float
                    if (simultaneous.size > 1) {
                        widthFactor = 1f / simultaneous.size
                        val pos = simultaneous.indexOf(event)
                        horizontalOffsetFactor = pos.toFloat()
                    } else {
                        widthFactor = 1f
                        horizontalOffsetFactor = 0f
                    }
                    val displayStart = if (currentStart < dayStart) dayStart else currentStart
                    val displayEnd = if (currentEnd > dayEnd) dayEnd else currentEnd
                    val startMin = (displayStart - dayStart) / 60000f
                    val durMin = (displayEnd - displayStart) / 60000f
                    if (durMin > 0) {
                        val eventColor = Color(event.color)
                        Card(
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = startMin * minuteHeightPx
                                    translationX = horizontalOffsetFactor * (with(density) { (400.dp.toPx()) }) * widthFactor
                                }
                                .fillMaxWidth(widthFactor)
                                .padding(start = TIME_COLUMN_WIDTH_DP.dp + 4.dp, end = 4.dp)
                                .height(with(density) { (durMin * minuteHeightPx).toDp() })
                                .zIndex(if (activeId == event.id) 1000f else 10f + idx)
                                .pointerInput(event.id, selectedDate) {
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = { activeId = event.id },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            val dMin = (dragAmount.y / minuteHeightPx).toLong()
                                            val duration = currentEnd - currentStart
                                            currentStart += dMin * 60000
                                            currentEnd = currentStart + duration
                                            if (change.position.y < 50f) {
                                                scope.launch { lazyListState.animateScrollBy(-30f) }
                                            } else if (change.position.y > (24 * HOUR_HEIGHT_DP * density.density - 50f)) {
                                                scope.launch { lazyListState.animateScrollBy(30f) }
                                            }
                                        },
                                        onDragEnd = {
                                            val updatedEvent = event.copy(timeInterval = TimeInterval(currentStart, currentEnd), lastTimeModified = currentDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                                            onUpdateActivity(updatedEvent)
                                            if (currentStart < dayStart || currentEnd > dayEnd) {
                                                onEventClick(updatedEvent)
                                            }
                                            activeId = null
                                        },
                                        onDragCancel = { activeId = null }
                                    )
                                }
                                .clickable { onEventClick(event) },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = eventColor)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(event.title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp, maxLines = 1)

                                val sT = Instant.ofEpochMilli(currentStart).atZone(ZoneId.systemDefault()).toLocalTime()
                                val eT = Instant.ofEpochMilli(currentEnd).atZone(ZoneId.systemDefault()).toLocalTime()
                                Text("%02d:%02d - %02d:%02d".format(sT.hour, sT.minute, eT.hour, eT.minute), color = Color.White.copy(0.8f), fontSize = 11.sp)

                                if (!event.location?.geocodedAddress.isNullOrBlank()) {

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = "Адрес",
                                            tint = Color.White.copy(alpha = 0.9f),
                                            modifier = Modifier.size(12.dp)
                                        )

                                        Spacer(modifier = Modifier.width(2.dp))

                                        event.location.geocodedAddress.let {
                                            Text(
                                                text = it,
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (isTodaySelected) {
                    val currentMinutes = currentDateTime.hour * 60 + currentDateTime.minute
                    val yPos = currentMinutes * minuteHeightPx
                    Box(modifier = Modifier.fillMaxWidth().graphicsLayer { translationY = yPos }.zIndex(600f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(thickness = 1.5.dp, color = MaterialTheme.colorScheme.inversePrimary)
                        }
                    }
                }
            }
        }
    }
}