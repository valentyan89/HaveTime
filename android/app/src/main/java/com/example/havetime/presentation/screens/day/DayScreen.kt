package com.example.havetime.presentation.screens.day

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Composable
fun DayScreen(
    selectedDate: LocalDate,
    intervals: List<Activity>,
    onIntervalCreated: (LocalDateTime, LocalDateTime) -> Unit,
    onIntervalClick: (Activity) -> Unit,
    onIntervalUpdated: (Activity) -> Unit,
    onDateViewed: (LocalDate) -> Unit
) {
    val hourHeight = 90.dp
    val density = LocalDensity.current
    val hourHeightPx = with(density) { hourHeight.toPx() }
    val minuteHeightPx = hourHeightPx / 60f
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val initialIndex = 5000
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    var activeId by remember { mutableStateOf<Int?>(null) }
    var isInteracting by remember { mutableStateOf(false) }

    var dragStartDT by remember { mutableStateOf<LocalDateTime?>(null) }
    var dragEndDT by remember { mutableStateOf<LocalDateTime?>(null) }

    LaunchedEffect(selectedDate) {
        val targetIndex = initialIndex + ChronoUnit.DAYS.between(LocalDate.now(), selectedDate).toInt()
        if (listState.firstVisibleItemIndex != targetIndex) {
            listState.scrollToItem(targetIndex)
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        val date = LocalDate.now().plusDays((listState.firstVisibleItemIndex - initialIndex).toLong())
        if (date != selectedDate) {
            onDateViewed(date)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize(),
            userScrollEnabled = !isInteracting && dragStartDT == null
        ) {
            items(10000, key = { it }) { index ->
                val dateOfRow = LocalDate.now().plusDays((index - initialIndex).toLong())
                val dayStart = LocalDateTime.of(dateOfRow, LocalTime.MIDNIGHT)

                Box(modifier = Modifier
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
                                dragStartDT?.let { s -> dragEndDT?.let { e ->
                                    val start = if (s.isBefore(e)) s else e
                                    var end = if (s.isBefore(e)) e else s
                                    if (ChronoUnit.MINUTES.between(start, end) < 10) end = start.plusMinutes(10)
                                    onIntervalCreated(start, end)
                                } }
                                dragStartDT = null; dragEndDT = null
                            }
                        )
                    }
                ) {
                    Column { repeat(24) { h ->
                        Box(
                            Modifier
                                .height(hourHeight)
                                .fillMaxWidth()
                        ) {
                            Text("${h}:00",
                                fontSize = 10.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(4.dp)
                            )
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(0.3f))
                        }
                    } }
                }
            }
        }

        intervals.forEach { interval ->
            var currentStart by remember(interval.id, interval.timeInterval.startTime, interval.color) { mutableStateOf(interval.timeInterval.startTime) }
            var currentEnd by remember(interval.id, interval.timeInterval.endTime, interval.color) { mutableStateOf(interval.timeInterval.endTime) }
            var currentOffset by remember(interval.id, interval.offsetX, interval.color) { mutableStateOf(interval.offsetX) }
            var currentWidthPx by remember(interval.id, interval.widthPx, interval.color) { mutableStateOf(interval.widthPx) }

            val isCaptured = activeId == interval.id
            
            val activityDate = currentStart.toLocalDate()
            val activityDayIndex = initialIndex + ChronoUnit.DAYS.between(LocalDate.now(), activityDate).toInt()
            
            val minutesFromMidnight = ChronoUnit.MINUTES.between(activityDate.atStartOfDay(), currentStart)
            val duration = ChronoUnit.MINUTES.between(currentStart, currentEnd)

            if (duration > 0) {
                Box(
                    modifier = Modifier
                    .then(if (currentWidthPx != null) Modifier.width(with(density) { currentWidthPx!!.toDp() }) else Modifier.fillMaxWidth())
                    .padding(start = (16 + currentOffset).dp, end = interval.paddingEnd.dp)
                    .graphicsLayer {
                        val scrollInPx = (listState.firstVisibleItemIndex - activityDayIndex) * (hourHeightPx * 24) + listState.firstVisibleItemScrollOffset
                        translationY = (minutesFromMidnight * minuteHeightPx) - scrollInPx
                        clip = false 
                    }
                    .height(with(density) { (duration * minuteHeightPx).toDp() })
                    .zIndex(if (isCaptured) 1000f else 1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(interval.color).copy(alpha = 0.9f))
                    .onSizeChanged { size ->
                        if (currentWidthPx == null) currentWidthPx = size.width.toFloat()
                    }
                    .pointerInput(interval.id) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { activeId = interval.id; isInteracting = true },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val dY = (dragAmount.y / minuteHeightPx).toLong()
                                val dX = dragAmount.x
                                
                                if (abs(dragAmount.y) > abs(dragAmount.x)) {
                                    currentStart = currentStart.plusMinutes(dY)
                                    currentEnd = currentEnd.plusMinutes(dY)
                                } else {
                                    currentWidthPx = ((currentWidthPx ?: 0f) + dX).coerceAtLeast(100f)
                                }
                            },
                            onDragEnd = {
                                onIntervalUpdated(interval.copy(
                                    timeInterval = TimeInterval(currentStart, currentEnd),
                                    offsetX = currentOffset,
                                    widthPx = currentWidthPx
                                ))
                                isInteracting = false; activeId = null
                            }
                        )
                    }
                    .clickable { onIntervalClick(interval) }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            interval.title,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text("${currentStart.format(timeFormatter)} - ${currentEnd.format(timeFormatter)}", color = Color.White.copy(0.8f), fontSize = 9.sp)
                    }
                    
                    Box(
                        Modifier.fillMaxWidth()
                            .height(24.dp)
                            .align(Alignment.TopCenter)
                            .pointerInput(interval.id) {
                        detectDragGestures(
                            onDragStart = { activeId = interval.id; isInteracting = true },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val d = (dragAmount.y / minuteHeightPx).toLong()
                                val next = currentStart.plusMinutes(d)
                                if (next.isBefore(currentEnd.minusMinutes(9))) currentStart = next
                            },
                            onDragEnd = { 
                                onIntervalUpdated(interval.copy(timeInterval = TimeInterval(currentStart, currentEnd)))
                                isInteracting = false; activeId = null 
                            }
                        )
                    })
                    
                    Box(Modifier.fillMaxWidth().height(24.dp).align(Alignment.BottomCenter).pointerInput(interval.id) {
                        detectDragGestures(
                            onDragStart = { activeId = interval.id; isInteracting = true },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val d = (dragAmount.y / minuteHeightPx).toLong()
                                val next = currentEnd.plusMinutes(d)
                                if (next.isAfter(currentStart.plusMinutes(9))) currentEnd = next
                            },
                            onDragEnd = { 
                                onIntervalUpdated(interval.copy(timeInterval = TimeInterval(currentStart, currentEnd)))
                                isInteracting = false; activeId = null 
                            }
                        )
                    })
                }
            }
        }
        
        // Превью создания новой активности (Тень)
        dragStartDT?.let { sDT -> dragEndDT?.let { eDT ->
            val s = if (sDT.isBefore(eDT)) sDT else eDT
            val e = if (sDT.isBefore(eDT)) eDT else sDT
            
            val dayOfS = s.toLocalDate()
            val daySIndex = initialIndex + ChronoUnit.DAYS.between(LocalDate.now(), dayOfS).toInt()
            val offMin = ChronoUnit.MINUTES.between(dayOfS.atStartOfDay(), s)
            val durMin = ChronoUnit.MINUTES.between(s, e)

            Box(Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    val scrollInPx = (listState.firstVisibleItemIndex - daySIndex) * (hourHeightPx * 24) + listState.firstVisibleItemScrollOffset
                    translationY = (offMin * minuteHeightPx) - scrollInPx
                }
                .height(with(density) { (durMin * minuteHeightPx).toDp() })
                .background(Color(0xFF664FA3).copy(0.4f), RoundedCornerShape(8.dp))
            )
        } }
    }
}
