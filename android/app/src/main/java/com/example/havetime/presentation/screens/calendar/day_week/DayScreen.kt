package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.presentation.navigation.Screen
import com.example.havetime.presentation.screens.calendar.CalendarMode
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.math.abs

private const val HOUR_HEIGHT_DP = 90f
private const val TIME_COLUMN_WIDTH_DP = 60f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    navController: NavController,
    initialDate: LocalDate = LocalDate.now(),
    sharedViewModel: MonthViewModel,
    viewModel: WeekDayViewModel = viewModel(factory = WeekDayViewModel.Factory),
    onAvatarClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
) {
    LaunchedEffect(initialDate) {
        viewModel.selectDate(initialDate)
    }

    val currentDate by viewModel.currentDate.collectAsState()
    val currentTime by viewModel.currentTime.collectAsState()
    val events by viewModel.activityForDate.collectAsState()
    val calendarMode by viewModel.calendarMode.collectAsState()
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }

    var showEventDialog by remember { mutableStateOf(false) }
    var editingActivity by remember { mutableStateOf<Activity?>(null) }

    var draftStartTime by remember { mutableStateOf<Long?>(null) }
    var draftEndTime by remember { mutableStateOf<Long?>(null) }

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
            Spacer(modifier = Modifier.statusBarsPadding())
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    ) {
                        if (!isSearchActive) {
                            Row(
                                modifier = Modifier.align(Alignment.CenterStart),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = onMenuClick) {
                                    Icon(Icons.Default.Menu, contentDescription = stringResource(R.string.menu))
                                }
                                
                                Spacer(modifier = Modifier.width(4.dp))

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .clickable { viewModel.go2Today() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = today.dayOfMonth.toString(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "${
                                    date.month.getDisplayName(
                                        TextStyle.FULL_STANDALONE,
                                        Locale("ru")
                                    ).replaceFirstChar { it.uppercase() }
                                } ${date.year}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20),
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .clickable { navController.navigate(Screen.Month.route) }
                            )

                            Row(
                                modifier = Modifier.align(Alignment.CenterEnd),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { isSearchActive = true }) {
                                    Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                                }
                                IconButton(onClick = onAvatarClick) {
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = stringResource(R.string.profile)
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { 
                                    isSearchActive = false
                                    viewModel.onSearchQueryChanged("")
                                }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                                }
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Поиск...") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(24.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF1B5E20),
                                        unfocusedBorderColor = Color.LightGray
                                    )
                                )
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                        Icon(Icons.Default.Close, contentDescription = null)
                                    }
                                }
                            }
                        }
                    }

                    if (!isSearchActive) {
                        val pagerState = rememberLazyListState(initialFirstVisibleItemIndex = 5000)
                        val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = pagerState)
                        
                        LaunchedEffect(date) {
                            val mondayOfDate = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            val mondayOfToday = LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                            val weeksDiff = ChronoUnit.WEEKS.between(mondayOfToday, mondayOfDate).toInt()
                            val target = 5000 + weeksDiff
                            if (pagerState.firstVisibleItemIndex != target) {
                                pagerState.animateScrollToItem(target)
                            }
                        }

                        LazyRow(
                            state = pagerState,
                            flingBehavior = snapFlingBehavior,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(10000) { weekIndex ->
                                val startOfWeek = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                                    .plusWeeks((weekIndex - 5000).toLong())
                                
                                Row(modifier = Modifier.fillParentMaxWidth()) {
                                    (0..6).forEach { dayOffset ->
                                        val itemDate = startOfWeek.plusDays(dayOffset.toLong())
                                        val isSelected = itemDate == date
                                        val isTodayItem = itemDate == today
                                        val dayOfWeekName = getShortDayOfWeekName(itemDate)

                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { viewModel.selectDate(itemDate) }
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = dayOfWeekName,
                                                fontSize = 12.sp,
                                                color = if (isSelected) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Box(
                                                modifier = Modifier
                                                    .size(38.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when {
                                                            isSelected -> Color(0xFF1B5E20)
                                                            isTodayItem -> Color(0xFF1B5E20).copy(alpha = 0.15f)
                                                            else -> Color.Transparent
                                                        }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = itemDate.dayOfMonth.toString(),
                                                    fontSize = 16.sp,
                                                    fontWeight = if (isSelected || isTodayItem) FontWeight.Bold else FontWeight.Normal,
                                                    color = when {
                                                        isSelected -> Color.White
                                                        isTodayItem -> Color(0xFF1B5E20)
                                                        else -> MaterialTheme.colorScheme.onSurface
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (isSearchActive && searchQuery.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .zIndex(100f)
                    ) {
                        items(searchResults) { activity ->
                            ListItem(
                                headlineContent = { Text(activity.title) },
                                supportingContent = { 
                                    val start = Instant.ofEpochMilli(activity.timeInterval.startTime).atZone(ZoneId.systemDefault())
                                    Text(String.format(Locale("ru"), "%d %s %d:%02d", start.dayOfMonth, start.month.getDisplayName(TextStyle.SHORT, Locale("ru")), start.hour, start.minute))
                                },
                                leadingContent = {
                                    Box(modifier = Modifier.size(12.dp).background(Color(activity.color), CircleShape))
                                },
                                modifier = Modifier.clickable {
                                    val activityDate = Instant.ofEpochMilli(activity.timeInterval.startTime).atZone(ZoneId.systemDefault()).toLocalDate()
                                    viewModel.selectDate(activityDate)
                                    isSearchActive = false
                                }
                            )
                            HorizontalDivider()
                        }
                        if (searchResults.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("Ничего не найдено", color = Color.Gray)
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
                    onAddActivity = { startTime, endTime ->
                        draftStartTime = startTime
                        draftEndTime = endTime
                        editingActivity = null
                        showEventDialog = true
                    },
                    onUpdateActivity = { activity ->
                        viewModel.updateActivity(activity)
                    },
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    if (showEventDialog) {
        val startInstant = editingActivity?.timeInterval?.startTime ?: draftStartTime
        val endInstant = editingActivity?.timeInterval?.endTime ?: draftEndTime

        AddActivityDialog(
            editingActivity = editingActivity,
            initialDate = startInstant?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            } ?: date,
            initialStartTime = startInstant?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
            } ?: currentTime.toLocalTime(),
            initialEndTime = endInstant?.let {
                Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalTime()
            } ?: currentTime.toLocalTime().plusHours(1),
            onDismiss = {
                showEventDialog = false
                editingActivity = null
                draftStartTime = null
                draftEndTime = null
            },
            onConfirm = { activity ->
                if (editingActivity != null) {
                    viewModel.updateActivity(activity)
                } else {
                    viewModel.addActivity(activity)
                }
                showEventDialog = false
                editingActivity = null
                draftStartTime = null
                draftEndTime = null
            },
            onDelete = { activityId ->
                viewModel.deleteActivity(activityId)
                showEventDialog = false
                editingActivity = null
                draftStartTime = null
                draftEndTime = null
            }
        )
    }
}

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
                                            val updatedEvent = event.copy(timeInterval = TimeInterval(currentStart, currentEnd), lastTimeModified = System.currentTimeMillis())
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
                            }
                        }
                    }
                }

                if (isTodaySelected) {
                    val currentMinutes = currentDateTime.hour * 60 + currentDateTime.minute
                    val yPos = currentMinutes * minuteHeightPx
                    Box(modifier = Modifier.fillMaxWidth().graphicsLayer { translationY = yPos }.zIndex(600f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).background(Color.Red, CircleShape))
                            HorizontalDivider(thickness = 1.5.dp, color = Color.Red)
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
    return date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ru"))
        .uppercase()
}
