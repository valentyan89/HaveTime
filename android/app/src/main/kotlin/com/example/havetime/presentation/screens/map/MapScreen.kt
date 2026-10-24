package com.example.havetime.presentation.screens.map

import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
import com.example.havetime.presentation.common.activity.AddActivityDialog
import com.example.havetime.presentation.common.navigation.bottom_bar.LocalSharedBottomBar
import com.example.havetime.presentation.common.navigation.bottom_bar.RegisterBottomBarAddAction
import com.example.havetime.presentation.navigation.Screen
import com.example.havetime.presentation.screens.calendar.month.MonthViewModel
import com.example.havetime.presentation.screens.map.components.MapContent
import com.example.havetime.presentation.screens.map.components.MapScreenHeader
import com.example.havetime.presentation.screens.map.components.MapSearchResults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(
    onOpenMonth: () -> Unit,
    sharedViewModel: MonthViewModel,
    viewModel: MapViewModel = hiltViewModel(),
    onAvatarClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val geoMarks by viewModel.geoMarksForDate.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val today by viewModel.today.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    var isSearchActive by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember { MapView(context) }

    val hazeState = LocalSharedBottomBar.current?.hazeState ?: remember { HazeState() }

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> mapView.onResume()
                    Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                    else -> {}
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    var selectedLocation by remember { mutableStateOf<Location?>(null) }
    var editingActivity by remember { mutableStateOf<Activity?>(null) }
    var showEventDialog by remember { mutableStateOf(false) }
    RegisterBottomBarAddAction(Screen.Map) {
        selectedLocation = null
        editingActivity = null
        showEventDialog = true
    }

    val currentLocale = LocalConfiguration.current.locales[0]
    val searchItemFormatter =
        remember(currentLocale) {
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(currentLocale)
        }

    LaunchedEffect(geoMarks, currentDate) {
        mapView.overlays.removeAll { it is Marker || it is MapEventsOverlay }

        val mapEventsReceiver =
            object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                    selectedLocation = Location(p.latitude, p.longitude, null)
                    editingActivity = null
                    showEventDialog = true
                    return true
                }

                override fun longPressHelper(p: GeoPoint): Boolean = false
            }
        mapView.overlays.add(MapEventsOverlay(mapEventsReceiver))

        geoMarks.forEach { activity ->
            activity.location?.let { location ->
                val marker =
                    Marker(mapView).apply {
                        position = GeoPoint(location.latitude, location.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        title = activity.title

                        val drawable =
                            GradientDrawable().apply {
                                shape = GradientDrawable.OVAL
                                setColor(activity.color)
                                setSize(65, 65)
                                setStroke(4, android.graphics.Color.WHITE)
                            }
                        icon = drawable

                        setOnMarkerClickListener { _, _ ->
                            editingActivity = activity
                            showEventDialog = true
                            true
                        }
                    }
                mapView.overlays.add(marker)
            }
        }

        mapView.invalidate()
    }

    LaunchedEffect(currentDate) {
        currentDate.let { sharedViewModel.selectDate(it) }
    }

    val date = currentDate
    val navigationBarsPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = {
            MapScreenHeader(
                modifier = Modifier.statusBarsPadding(),
                date = date,
                today = today,
                currentLocale = currentLocale,
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchActiveChanged = { isSearchActive = it },
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onGo2Today = viewModel::go2Today,
                onMonthClick = {
                    onOpenMonth()
                },
                onMenuClick = onMenuClick,
                onAvatarClick = onAvatarClick,
                onSelectDate = viewModel::selectDate
            )
        }
    ) { paddingValues ->
        Column(
            modifier =
            Modifier
                .haze(
                    hazeState,
                    backgroundColor = MaterialTheme.colorScheme.background,
                    tint = Color.Black.copy(alpha = .2f),
                    blurRadius = 30.dp
                ).fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = navigationBarsPadding
                )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (isSearchActive && searchQuery.isNotEmpty()) {
                    MapSearchResults(searchResults, searchItemFormatter) { activity ->
                        val activityDate = Instant.ofEpochMilli(activity.timeInterval.startTime)
                            .atZone(ZoneId.systemDefault()).toLocalDate()
                        viewModel.selectDate(activityDate)
                        isSearchActive = false
                    }
                }

                MapContent(
                    mapView = mapView,
                    geoMarks = geoMarks,
                    paddingValues = PaddingValues(bottom = 80.dp + navigationBarsPadding)
                )
            }
        }
    }

    if (showEventDialog) {
        AddActivityDialog(
            editingActivity = editingActivity,
            initialDate = date,
            initialStartTime = LocalTime.now(),
            initialEndTime = LocalTime.now().plusHours(1),
            initialLocation = selectedLocation,
            onDismiss = {
                showEventDialog = false
                selectedLocation = null
                editingActivity = null
            },
            onConfirm = { activity ->
                if (editingActivity != null) {
                    viewModel.updateActivity(activity)
                } else {
                    viewModel.addActivity(activity)
                }
                showEventDialog = false
                selectedLocation = null
                editingActivity = null
            },
            onDelete = { activityId ->
                viewModel.deleteActivity(activityId)
                showEventDialog = false
                selectedLocation = null
                editingActivity = null
            }
        )
    }
}
