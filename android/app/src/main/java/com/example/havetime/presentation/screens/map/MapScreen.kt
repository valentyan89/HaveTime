package com.example.havetime.presentation.screens.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.havetime.presentation.screens.calendar.day_week.getShortDayOfWeekName
import com.example.havetime.presentation.screens.calendar.day_week.getWeekDays
import com.example.havetime.presentation.navigation.Screen
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel

private const val SWIPE_THRESHOLD = 50f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    initialDate: LocalDate = LocalDate.now(),
    sharedViewModel: MonthViewModel,
    viewModel: MapViewModel = viewModel(factory = MapViewModel.Factory),
    onAvatarClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    LaunchedEffect(initialDate) {
        viewModel.selectDate(initialDate)
    }
    val context = LocalContext.current
    val geoMarks by viewModel.geoMarksForDate.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()

    val mapView = remember { MapView(context) }

    LaunchedEffect(geoMarks, currentDate) {
        mapView.overlays.removeAll { it is Marker }
        geoMarks.forEach { activity ->
            activity.location?.let { location ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(location.latitude, location.longitude)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = activity.title
                }
                mapView.overlays.add(marker)
            }
        }
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
                    IconButton(onClick = onMenuClick) {
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
                            modifier = Modifier.clickable { navController.popBackStack() }
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
                                            viewModel.go2NextDay()
                                        } else if (totalDragAmount > SWIPE_THRESHOLD) {
                                            viewModel.go2PrevDay()
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
                                                isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
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
}