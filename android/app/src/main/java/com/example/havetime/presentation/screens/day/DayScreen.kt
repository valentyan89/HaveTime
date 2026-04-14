package com.example.havetime.presentation.screens.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.havetime.domain.model.TimeInterval
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun DayScreen(
    intervals: List<TimeInterval>,
    onIntervalCreated: (LocalTime, LocalTime) -> Unit,
    onIntervalClick: (TimeInterval) -> Unit,
    onIntervalUpdated: (TimeInterval) -> Unit
) {
    val hourHeight = 90.dp
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val minuteHeight = hourHeight.value / 60f
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    var dragStartTime by remember { mutableStateOf<LocalTime?>(null) }
    var dragEndTime by remember { mutableStateOf<LocalTime?>(null) }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(0.12f).verticalScroll(scrollState)) {
            Column {
                repeat(24) { h ->
                    Box(Modifier.height(hourHeight), contentAlignment = Alignment.TopCenter) {
                        Text(String.format("%02d:00", h), fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .weight(0.78f)
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(hourHeight * 24)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val totalMinutes = (offset.y / with(density) { minuteHeight.dp.toPx() }).toInt()
                                dragStartTime = LocalTime.of((totalMinutes / 60).coerceIn(0, 23), (totalMinutes % 60).coerceIn(0, 59))
                                dragEndTime = dragStartTime
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val deltaMinutes = (dragAmount.y / with(density) { minuteHeight.dp.toPx() }).toLong()
                                dragEndTime?.let { try { dragEndTime = it.plusMinutes(deltaMinutes) } catch(e: Exception) {} }
                            },
                            onDragEnd = {
                                if (dragStartTime != null && dragEndTime != null && dragStartTime != dragEndTime) {
                                    onIntervalCreated(
                                        if (dragStartTime!!.isBefore(dragEndTime)) dragStartTime!! else dragEndTime!!,
                                        if (dragStartTime!!.isBefore(dragEndTime)) dragEndTime!! else dragStartTime!!
                                    )
                                }
                                dragStartTime = null; dragEndTime = null
                            }
                        )
                    }
            ) {
                Column { repeat(24) { Box(Modifier.height(hourHeight).fillMaxWidth()) { HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(0.3f)) } } }
                val sortedIntervals = intervals.sortedBy { it.start }
                sortedIntervals.forEachIndexed { index, interval ->
                    val startMinutes = interval.start.hour * 60 + interval.start.minute
                    val duration = ChronoUnit.MINUTES.between(interval.start, interval.end).toInt()
                    val overlapLevel = sortedIntervals.take(index).count { prev ->
                        interval.start.toLocalTime().isBefore(prev.end.toLocalTime()) && prev.start.toLocalTime().isBefore(interval.end.toLocalTime())
                    }
                    var currentStart by remember(interval) { mutableStateOf(interval.start.toLocalTime()) }
                    var currentEnd by remember(interval) { mutableStateOf(interval.end.toLocalTime()) }
                    var currentOffset by remember(interval) { mutableStateOf(overlapLevel * 16f) }
                    val displayStartMinutes = currentStart.hour * 60 + currentStart.minute
                    val displayDuration = ChronoUnit.MINUTES.between(currentStart, currentEnd).toInt()

                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = currentOffset.dp)
                        .offset(y = (displayStartMinutes * minuteHeight).dp)
                        .height((displayDuration * minuteHeight).dp)
                        .zIndex(index.toFloat())
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(interval.color).copy(0.9f))
                        .clickable { onIntervalClick(interval) }
                    ) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "${interval.title}\n${currentStart.format(timeFormatter)} - ${currentEnd.format(timeFormatter)}",
                            fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold
                        )
                        Box(Modifier.fillMaxWidth().height(12.dp).align(Alignment.TopCenter)
                            .pointerInput(interval) {
                                detectDragGestures(onDrag = { change, dragAmount ->
                                    change.consume()
                                    val delta = (dragAmount.y / with(density) { minuteHeight.dp.toPx() }).toLong()
                                    val newStart = currentStart.plusMinutes(delta)
                                    if (newStart.isBefore(currentEnd)) currentStart = newStart
                                }, onDragEnd = {
                                    onIntervalUpdated(interval.copy(start = interval.start.with(currentStart)))
                                })
                            }
                        )
                        Box(Modifier.fillMaxWidth().height(12.dp).align(Alignment.BottomCenter)
                            .pointerInput(interval) {
                                detectDragGestures(onDrag = { change, dragAmount ->
                                    change.consume()
                                    val delta = (dragAmount.y / with(density) { minuteHeight.dp.toPx() }).toLong()
                                    val newEnd = currentEnd.plusMinutes(delta)
                                    if (newEnd.isAfter(currentStart)) currentEnd = newEnd
                                }, onDragEnd = {
                                    onIntervalUpdated(interval.copy(end = interval.end.with(currentEnd)))
                                })
                            }
                        )
                        Box(Modifier.width(16.dp).fillMaxHeight().align(Alignment.CenterStart)
                            .pointerInput(interval) {
                                detectDragGestures(onDrag = { change, dragAmount ->
                                    change.consume()
                                    currentOffset = (currentOffset + dragAmount.x / density.density).coerceAtLeast(0f)
                                }, onDragEnd = {
                                })
                            }
                        )
                    }
                }
                if (dragStartTime != null && dragEndTime != null) {
                    val s = if (dragStartTime!!.isBefore(dragEndTime)) dragStartTime!! else dragEndTime!!
                    val e = if (dragStartTime!!.isBefore(dragEndTime)) dragEndTime!! else dragStartTime!!
                    Box(Modifier.fillMaxWidth().offset(y = ((s.hour * 60 + s.minute) * minuteHeight).dp)
                        .height((ChronoUnit.MINUTES.between(s, e) * minuteHeight).dp)
                        .background(Color(0xFF664FA3).copy(0.4f), RoundedCornerShape(8.dp)).zIndex(100f))
                }
            }
        }
        Box(Modifier.weight(0.1f).fillMaxHeight().verticalScroll(scrollState)) { Spacer(Modifier.height(hourHeight * 24)) }
    }
}