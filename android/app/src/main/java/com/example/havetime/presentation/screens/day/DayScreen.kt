package com.example.havetime.presentation.screens.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.TimeInterval
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Composable
fun DayScreen(
    selectedDate: LocalDate,
    currentTime: LocalDateTime,
    intervals: List<Activity>,
    onIntervalCreated: (LocalDateTime, LocalDateTime) -> Unit,
    onIntervalClick: (Activity) -> Unit,
    onIntervalUpdated: (Activity) -> Unit,
    onDateChanged: (LocalDate) -> Unit
) {
    val hourHeight = 90.dp
    val density = LocalDensity.current
    val hourHeightPx = with(density) { hourHeight.toPx() }
    val minuteHeightPx = hourHeightPx / 60f
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val scope = rememberCoroutineScope()
    
    val initialIndex = 5000
    val pagerState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapBehavior = rememberSnapFlingBehavior(pagerState)

    var activeId by remember { mutableStateOf<String?>(null) }
    var isInteracting by remember { mutableStateOf(false) }

    var dragStartDT by remember { mutableStateOf<LocalDateTime?>(null) }
    var dragEndDT by remember { mutableStateOf<LocalDateTime?>(null) }

    LaunchedEffect(selectedDate) {
        val daysDiff = ChronoUnit.DAYS.between(LocalDate.now(), selectedDate).toInt()
        val targetIndex = initialIndex + daysDiff
        if (pagerState.firstVisibleItemIndex != targetIndex) {
            pagerState.animateScrollToItem(targetIndex)
        }
    }

    LaunchedEffect(pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress && !isInteracting && dragStartDT == null) {
            val dateOffset = pagerState.firstVisibleItemIndex - initialIndex
            val newDate = LocalDate.now().plusDays(dateOffset.toLong())
            if (newDate != selectedDate) {
                onDateChanged(newDate)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            state = pagerState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = !isInteracting && dragStartDT == null
        ) {
            items(10000) { index ->
                val dateOfRow = LocalDate.now().plusDays((index - initialIndex).toLong())
                val dayStart = dateOfRow.atStartOfDay()
                val dayEnd = dateOfRow.plusDays(1).atStartOfDay()
                val scrollState = rememberScrollState()

                LaunchedEffect(Unit) {
                    if (dateOfRow == currentTime.toLocalDate()) {
                        val currentMinutes = currentTime.hour * 60 + currentTime.minute
                        val targetScroll = (currentMinutes * minuteHeightPx - 400f).coerceAtLeast(0f)
                        scrollState.scrollTo(targetScroll.toInt())
                    }
                }

                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(hourHeight * 24)
                            .pointerInput(dateOfRow) {
                                detectDragGesturesAfterLongPress(
                                    onDragStart = { offset ->
                                        val totalMin = (offset.y / minuteHeightPx).toLong()
                                        dragStartDT = dayStart.plusMinutes(totalMin)
                                        dragEndDT = dragStartDT
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragEndDT = dragEndDT?.plusMinutes((dragAmount.y / minuteHeightPx).toLong())
                                    },
                                    onDragEnd = {
                                        dragStartDT?.let { s ->
                                            dragEndDT?.let { e ->
                                                val start = if (s.isBefore(e)) s else e
                                                var end = if (s.isBefore(e)) e else s
                                                if (ChronoUnit.MINUTES.between(start, end) < 10) end = start.plusMinutes(10)
                                                onIntervalCreated(start, end)
                                            }
                                        }
                                        dragStartDT = null; dragEndDT = null
                                    }
                                )
                            }
                    ) {
                        Column {
                            repeat(24) { h ->
                                Box(Modifier.height(hourHeight).fillMaxWidth()) {
                                    Text("${h}:00", fontSize = 10.sp, color = Color.Gray, modifier = Modifier.padding(4.dp))
                                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(0.3f))
                                }
                            }
                        }

                        // ТЕНЬ ПРИ СОЗДАНИИ (Восстановлена)
                        dragStartDT?.let { sDT -> dragEndDT?.let { eDT ->
                            val s = if (sDT.isBefore(eDT)) sDT else eDT
                            val e = if (sDT.isBefore(eDT)) eDT else sDT
                            if (s.isBefore(dayEnd) && e.isAfter(dayStart)) {
                                val dS = if (s.isBefore(dayStart)) dayStart else s
                                val dE = if (e.isAfter(dayEnd)) dayEnd else e
                                val offMin = ChronoUnit.MINUTES.between(dayStart, dS)
                                val durMin = ChronoUnit.MINUTES.between(dS, dE)
                                Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).graphicsLayer { translationY = offMin * minuteHeightPx }.height(with(density) { (durMin * minuteHeightPx).toDp() }).background(Color(0xFF664FA3).copy(0.4f), RoundedCornerShape(8.dp)).zIndex(400f))
                            }
                        } }

                        if (currentTime.toLocalDate() == dateOfRow) {
                            val currentMinutes = currentTime.hour * 60 + currentTime.minute
                            val yPos = currentMinutes * minuteHeightPx
                            Box(modifier = Modifier.fillMaxWidth().graphicsLayer { translationY = yPos }.zIndex(600f)) {
                                HorizontalDivider(thickness = 2.dp, color = Color.Red)
                                Box(modifier = Modifier.size(10.dp).offset(x = (-5).dp).background(Color.Red, RoundedCornerShape(5.dp)).align(Alignment.CenterStart))
                            }
                        }

                        // Логика колонок и пересечений
                        val dayVisibleIntervals = intervals.filter { 
                            it.timeInterval.start.isBefore(dayEnd) && it.timeInterval.end.isAfter(dayStart) 
                        }.sortedBy { it.timeInterval.start }

                        dayVisibleIntervals.forEachIndexed { idx, interval ->
                            var currentStart by remember(interval.id, interval.timeInterval.start, interval.color) { mutableStateOf(interval.timeInterval.start) }
                            var currentEnd by remember(interval.id, interval.timeInterval.end, interval.color) { mutableStateOf(interval.timeInterval.end) }

                            val overlapping = dayVisibleIntervals.filter { 
                                it.timeInterval.start.isBefore(interval.timeInterval.end) && 
                                it.timeInterval.end.isAfter(interval.timeInterval.start)
                            }
                            val simultaneous = overlapping.filter { it.timeInterval.start == interval.timeInterval.start }
                            
                            val widthFactor: Float
                            val horizontalOffset: Float
                            
                            if (simultaneous.size > 1) {
                                widthFactor = 1f / simultaneous.size
                                val pos = simultaneous.indexOf(interval)
                                horizontalOffset = pos.toFloat()
                            } else {
                                widthFactor = 1f
                                horizontalOffset = 0f
                            }

                            val displayStart = if (currentStart.isBefore(dayStart)) dayStart else currentStart
                            val displayEnd = if (currentEnd.isAfter(dayEnd)) dayEnd else currentEnd
                            val startMin = ChronoUnit.MINUTES.between(dayStart, displayStart)
                            val durationMin = ChronoUnit.MINUTES.between(displayStart, displayEnd)

                            if (durationMin > 0) {
                                Box(modifier = Modifier
                                    .graphicsLayer { 
                                        translationY = startMin * minuteHeightPx
                                        translationX = horizontalOffset * (400.dp.toPx()) * widthFactor
                                    }
                                    .fillMaxWidth(widthFactor)
                                    .padding(horizontal = 4.dp)
                                    .height(with(density) { (durationMin * minuteHeightPx).toDp() })
                                    .zIndex(if (activeId == interval.id) 1000f else 10f + idx)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(interval.color).copy(alpha = 0.9f))
                                    .pointerInput(interval.id, dateOfRow) {
                                        detectDragGesturesAfterLongPress(
                                            onDragStart = { activeId = interval.id; isInteracting = true },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                val dMin = (dragAmount.y / minuteHeightPx).toLong()
                                                if (abs(dragAmount.y) > abs(dragAmount.x)) {
                                                    val duration = ChronoUnit.MINUTES.between(currentStart, currentEnd)
                                                    currentStart = currentStart.plusMinutes(dMin)
                                                    currentEnd = currentStart.plusMinutes(duration)
                                                    
                                                    if (currentStart.isBefore(dayStart)) {
                                                        scope.launch { pagerState.animateScrollToItem(pagerState.firstVisibleItemIndex - 1) }
                                                    } else if (currentEnd.isAfter(dayEnd)) {
                                                        scope.launch { pagerState.animateScrollToItem(pagerState.firstVisibleItemIndex + 1) }
                                                    }
                                                }
                                            },
                                            onDragEnd = {
                                                onIntervalUpdated(interval.copy(timeInterval = TimeInterval(currentStart, currentEnd)))
                                                isInteracting = false; activeId = null
                                            }
                                        )
                                    }
                                    .clickable { onIntervalClick(interval) }
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(interval.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text("${currentStart.format(timeFormatter)} - ${currentEnd.format(timeFormatter)}", color = Color.White.copy(0.8f), fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
