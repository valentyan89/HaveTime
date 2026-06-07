package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.screens.calendar.CalendarMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.example.havetime.R
import androidx.compose.ui.unit.IntOffset
import com.example.havetime.presentation.navigation.Screen
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt
import java.time.LocalDateTime

private const val SWIPE_THRESHOLD = 50f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    navController: NavController,
    initialDate: LocalDate = LocalDate.now(),
    sharedViewModel: MonthViewModel,
    viewModel: WeekDayViewModel = viewModel(factory = WeekDayViewModel.Factory),
    onAvatarClick: () -> Unit = {},
) {
    LaunchedEffect(initialDate) {
        viewModel.selectDate(initialDate)
    }

    val currentDate by viewModel.currentDate.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val events by viewModel.activityForDate.collectAsState()
    val calendarMode by viewModel.calendarMode.collectAsState()
    var showEventDialog by remember { mutableStateOf(false) }
    var editingActivity by remember { mutableStateOf<Activity?>(null) }

    LaunchedEffect(Unit) {
        if (calendarMode != CalendarMode.WEEK_DAY) {
            viewModel.setCalendarMode(CalendarMode.WEEK_DAY)
        }
    }

    if (currentDate == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    LaunchedEffect(currentDate) {
        currentDate?.let { sharedViewModel.selectDate(it) }
    }

    val date = currentDate!!
    val today = LocalDate.now()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = stringResource(R.string.search)
                        )
                    }
                    IconButton(onClick = onAvatarClick) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = stringResource(R.string.profile)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showEventDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_event)
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = stringResource(R.string.calendar)
                            )
                        },
                        label = { Text(stringResource(R.string.calendar)) },
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = stringResource(R.string.map)
                            )
                        },
                        label = { Text(stringResource(R.string.map)) },
                        selected = false,
                        onClick = { navController.navigate(Screen.Map.route) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${
                                date.month.getDisplayName(
                                    TextStyle.FULL_STANDALONE,
                                    Locale("ru")
                                ).replaceFirstChar { it.uppercase() }
                            } ${date.year}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { navController.navigate(Screen.Month.route) }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .pointerInput(Unit) {
                                var totalDragAmount = 0f
                                detectHorizontalDragGestures(
                                    onDragStart = { totalDragAmount = 0f },
                                    onDragEnd = {
                                        if (totalDragAmount < -SWIPE_THRESHOLD) {
                                            viewModel.go2NextWeek()
                                        } else if (totalDragAmount > SWIPE_THRESHOLD) {
                                            viewModel.go2PrevWeek()
                                        }
                                    },
                                    onDragCancel = { totalDragAmount = 0f },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        totalDragAmount += dragAmount
                                    }
                                )
                            },
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val weekDays = getWeekDays(date)

                        weekDays.forEach { dayDate ->
                            val isSelected = dayDate == date
                            val isToday = dayDate == today
                            val dayOfWeekName = getShortDayOfWeekName(dayDate)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.selectDate(dayDate) }
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = dayOfWeekName,
                                    fontSize = 13.sp,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.primary.copy(
                                                    alpha = 0.2f
                                                )

                                                else -> Color.Transparent
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayDate.dayOfMonth.toString(),
                                        fontSize = 17.sp,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isSelected -> MaterialTheme.colorScheme.onPrimary
                                            isToday -> MaterialTheme.colorScheme.primary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            DayTimeline(
                events = events,
                currentDateTime = currentTime,
                selectedDate = date,
                onEventClick = { activity ->
                    editingActivity = activity
                    showEventDialog = true
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    if (showEventDialog) {
        AddActivityDialog(
            editingActivity = editingActivity,  // ← передаём редактируемое событие
            initialDate = editingActivity?.let {
                Instant.ofEpochMilli(it.timeInterval.startTime).atZone(ZoneId.systemDefault())
                    .toLocalDate()
            } ?: date,
            initialStartTime = editingActivity?.let {
                Instant.ofEpochMilli(it.timeInterval.startTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            } ?: currentTime.toLocalTime(),
            initialEndTime = editingActivity?.let {
                Instant.ofEpochMilli(it.timeInterval.endTime).atZone(ZoneId.systemDefault())
                    .toLocalTime()
            } ?: currentTime.toLocalTime().plusHours(1),
            onDismiss = {
                showEventDialog = false
                editingActivity = null
            },
            onConfirm = { activity ->
                if (editingActivity != null) {
                    viewModel.updateActivity(activity)  // ← обновление
                } else {
                    viewModel.addActivity(activity)     // ← создание
                }
                showEventDialog = false
                editingActivity = null
            },
            onDelete = { activityId ->
                viewModel.deleteActivity(activityId)
                showEventDialog = false
                editingActivity = null
            }
        )
    }
}

private const val HOUR_HEIGHT_DP = 80f
private const val TIME_COLUMN_WIDTH_DP = 60f

@Composable
fun DayTimeline(
    events: List<Activity>,
    currentDateTime: LocalDateTime,
    selectedDate: LocalDate,
    onEventClick: (Activity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()
    val density = LocalDensity.current
    val isTodaySelected = selectedDate == currentDateTime.toLocalDate()

    LaunchedEffect(key1 = isTodaySelected) {
        if (isTodaySelected) {
            val currentHour = currentDateTime.hour
            val targetHour = (currentHour - 2).coerceAtLeast(0)
            val targetOffsetPx = with(density) { (targetHour * HOUR_HEIGHT_DP).dp.toPx() }
            val delta = targetOffsetPx - lazyListState.firstVisibleItemScrollOffset
            if (delta != 0f) lazyListState.animateScrollBy(delta)
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

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = TIME_COLUMN_WIDTH_DP.dp)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
                                .align(Alignment.TopStart)
                        )
                    }
                }

                events.forEach { event ->
                    val start = Instant.ofEpochMilli(event.timeInterval.startTime).atZone(ZoneId.systemDefault())
                    val end = Instant.ofEpochMilli(event.timeInterval.endTime).atZone(ZoneId.systemDefault())

                    val startMinutes = start.hour * 60 + start.minute
                    val durationMinutes = ((event.timeInterval.endTime - event.timeInterval.startTime) / 60000f).coerceAtLeast(30f)

                    val minuteHeight = HOUR_HEIGHT_DP / 60f
                    val topOffset = startMinutes * minuteHeight
                    val cardHeight = durationMinutes * minuteHeight

                    val eventColor = Color(event.color)

                    Card(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = TIME_COLUMN_WIDTH_DP.dp.roundToPx(),
                                    y = topOffset.dp.roundToPx()
                                )
                            }
                            .padding(end = 16.dp, start = 8.dp)
                            .fillMaxWidth()
                            .height(cardHeight.dp)
                            .clickable { onEventClick(event) },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = eventColor)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = event.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,  // ← белый текст на цветном фоне
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "%02d:%02d - %02d:%02d".format(start.hour, start.minute, end.hour, end.minute),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)  // ← полупрозрачный белый
                            )
                        }
                    }
                }

                if (isTodaySelected) {
                    val currentMinutes = currentDateTime.hour * 60 + currentDateTime.minute
                    val minuteHeight = HOUR_HEIGHT_DP / 60f
                    val lineTopOffsetDp = (currentMinutes * minuteHeight).dp

                    Row(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    x = (TIME_COLUMN_WIDTH_DP - 3).dp.roundToPx(),
                                    y = lineTopOffsetDp.roundToPx()
                                )
                            }
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color.Red, shape = CircleShape)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.5.dp)
                                .background(Color.Red)
                        )
                    }
                }
            }
        }
    }
}

fun getWeekDays(centerDate: LocalDate): List<LocalDate> {
    var monday = centerDate
    while (monday.dayOfWeek != DayOfWeek.MONDAY) {
        monday = monday.minusDays(1)
    }
    return (0..6).map { monday.plusDays(it.toLong()) }
}

fun getShortDayOfWeekName(date: LocalDate): String {
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
        .uppercase()
}