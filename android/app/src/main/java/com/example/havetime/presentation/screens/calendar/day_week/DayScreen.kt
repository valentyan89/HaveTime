package com.example.havetime.presentation.screens.calendar.day_week


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.screens.calendar.CalendarMode
import com.example.havetime.presentation.screens.calendar.day_week.WeekDayViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.example.havetime.R
import androidx.compose.ui.unit.IntOffset
import com.example.havetime.presentation.navigation.Screen
import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt
import com.example.havetime.presentation.screens.calendar.day_week.AddActivityDialog

private const val SWIPE_THRESHOLD = 50f


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    navController: NavController,
    viewModel: WeekDayViewModel = viewModel(factory = WeekDayViewModel.Factory),
    onMonthClick: () -> Unit = {}
) {
    val currentDate by viewModel.currentDate.collectAsState()
    val events by viewModel.activityForDate.collectAsState()
    val calendarMode by viewModel.calendarMode.collectAsState()
    var showEventDialog by remember { mutableStateOf(false) }


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

    val date = currentDate!!

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
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more))
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
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.calendar)) },
                        label = { Text(stringResource(R.string.calendar))},
                        selected = true,
                        onClick = { }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Map, contentDescription = stringResource(R.string.map)) },
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
                            text = "${date.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).replaceFirstChar { it.uppercase() }} ${date.year}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { onMonthClick() }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .pointerInput(Unit) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {},
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()

                                        if (dragAmount < -SWIPE_THRESHOLD) {
                                            viewModel.go2NextWeek()
                                        } else if (dragAmount > SWIPE_THRESHOLD) {
                                            viewModel.go2PrevWeek()
                                        }
                                    }
                                )
                            },
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val weekDays = getWeekDays(date)

                        weekDays.forEach { dayDate ->
                            val isSelected = dayDate == date
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
                                            if (isSelected) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                Color.Transparent
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayDate.dayOfMonth.toString(),
                                        fontSize = 17.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
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
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
    if (showEventDialog) {
        AddActivityDialog(
            editingActivity = null,
            initialDate = date,
            initialStartTime = java.time.LocalTime.of(12, 0),
            initialEndTime = java.time.LocalTime.of(13, 0),
            onDismiss = { showEventDialog = false },
            onConfirm = { activity ->
                viewModel.addActivity(activity)
                showEventDialog = false
            },
            onDelete = { activityId ->
                viewModel.deleteActivity(activityId)
                showEventDialog = false
            }
        )
    }
}

private const val HOUR_HEIGHT_DP = 80f
private const val TIME_COLUMN_WIDTH_DP = 60f

@Composable
fun DayTimeline(
    events: List<Activity>,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier
    ) {

        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((24 * HOUR_HEIGHT_DP).dp)
            ) {

                Column {

                    repeat(24) { hour ->

                        Row(
                            modifier = Modifier.height(HOUR_HEIGHT_DP.dp)
                        ) {

                            Text(
                                text = String.format(Locale.getDefault(), "%02d:00", hour),
                                modifier = Modifier
                                    .width(TIME_COLUMN_WIDTH_DP.dp)
                                    .padding(top = 4.dp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                            .copy(alpha = 0.3f)
                                    )
                            )
                        }
                    }
                }

                events.forEach { event ->

                    val start =
                        Instant.ofEpochMilli(
                            event.timeInterval.startTime
                        ).atZone(
                            ZoneId.systemDefault()
                        )

                    val end =
                        Instant.ofEpochMilli(
                            event.timeInterval.endTime
                        ).atZone(
                            ZoneId.systemDefault()
                        )

                    val startMinutes =
                        start.hour * 60 + start.minute

                    val durationMinutes =
                        ((event.timeInterval.endTime -
                                event.timeInterval.startTime) / 60000f)
                            .coerceAtLeast(30f)

                    val minuteHeight =
                        HOUR_HEIGHT_DP / 60f

                    val topOffset =
                        startMinutes * minuteHeight

                    val cardHeight =
                        durationMinutes * minuteHeight

                    Card(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    TIME_COLUMN_WIDTH_DP.dp.roundToPx(),
                                    topOffset.roundToInt()
                                )
                            }
                            .padding(horizontal = 8.dp)
                            .fillMaxWidth(0.85f)
                            .height(cardHeight.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {

                            Text(
                                text = event.title,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text = "%02d:%02d - %02d:%02d".format(
                                    start.hour,
                                    start.minute,
                                    end.hour,
                                    end.minute
                                ),
                                fontSize = 12.sp
                            )
                        }
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
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        .uppercase()
}