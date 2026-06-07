package com.example.havetime.presentation.screens.map

import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.havetime.R
import com.example.havetime.domain.model.Location
import com.example.havetime.presentation.screens.calendar.day_week.getShortDayOfWeekName
import com.example.havetime.presentation.screens.calendar.day_week.getWeekDays
import com.example.havetime.presentation.navigation.Screen
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel

private const val SWIPE_THRESHOLD = 50f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    initialDate: LocalDate = LocalDate.now(),
    sharedViewModel: MonthViewModel,
    viewModel: MapViewModel = viewModel(factory = MapViewModel.Factory),
    onAvatarClick: () -> Unit = {}
) {
    LaunchedEffect(initialDate) {
        viewModel.selectDate(initialDate)
    }
    val context = LocalContext.current
    val geoMarks by viewModel.geoMarksForDate.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()

    val mapView = remember { MapView(context) }
    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var showEventDialog by remember { mutableStateOf(false) }

    LaunchedEffect(geoMarks, currentDate) {
        mapView.overlays.removeAll { it is Marker || it is MapEventsOverlay }
        
        geoMarks.forEach { activity ->
            activity.location?.let { location ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(location.latitude, location.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = activity.title
                    
                    val drawable = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(activity.color)
                        setSize(60, 60)
                        setStroke(4, android.graphics.Color.WHITE)
                    }
                    icon = drawable
                }
                mapView.overlays.add(marker)
            }
        }

        val mapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                selectedLocation = Location(p.latitude, p.longitude, null)
                showEventDialog = true
                return true
            }
            override fun longPressHelper(p: GeoPoint): Boolean = false
        }
        mapView.overlays.add(MapEventsOverlay(mapEventsReceiver))
        
        mapView.invalidate()
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
                        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search))
                    }
                    IconButton(onClick = onAvatarClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.profile))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
                        label = { Text(stringResource(R.string.calendar)) },
                        selected = false,
                        onClick = { navController.popBackStack() }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Map, contentDescription = stringResource(R.string.map)) },
                        label = { Text(stringResource(R.string.map)) },
                        selected = true,
                        onClick = { }
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
                            text = "${date.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale("ru")).replaceFirstChar { it.uppercase() }} ${date.year}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable { 
                                navController.navigate(Screen.Month.route) 
                            }
                        )
                    }
                    val pagerState = rememberLazyListState(initialFirstVisibleItemIndex = 5000)
                    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = pagerState)
                    
                    LaunchedEffect(date) {
                        val mondayOfDate = date.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                        val mondayOfToday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
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
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        isSelected -> MaterialTheme.colorScheme.primary
                                                        isTodayItem -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
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
                                                    isSelected -> MaterialTheme.colorScheme.onPrimary
                                                    isTodayItem -> MaterialTheme.colorScheme.primary
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

            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AndroidView(
                        factory = {
                            mapView.apply {
                                setMultiTouchControls(true)
                                controller.setZoom(15.0)
                                controller.setCenter(GeoPoint(55.7558, 37.6173))
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    if (geoMarks.isEmpty()) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = stringResource(R.string.no_location_events),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEventDialog && selectedLocation != null) {
        com.example.havetime.presentation.screens.calendar.day_week.AddActivityDialog(
            editingActivity = null,
            initialDate = date,
            initialStartTime = LocalTime.now(),
            initialEndTime = LocalTime.now().plusHours(1),
            onDismiss = {
                showEventDialog = false
                selectedLocation = null
            },
            onConfirm = { activity ->
                val activityWithLocation = activity.copy(location = selectedLocation)
                viewModel.addActivity(activityWithLocation)
                showEventDialog = false
                selectedLocation = null
            },
            onDelete = { activityId ->
                showEventDialog = false
                selectedLocation = null
            }
        )
    }
}