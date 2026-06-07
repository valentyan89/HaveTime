package com.example.havetime.presentation.screens.calendar.day_week

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.havetime.R
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
import com.example.havetime.domain.model.TimeInterval
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

private val EventColors = listOf(
    R.color.event_red,
    R.color.event_blue,
    R.color.event_green,
    R.color.event_purple,
    R.color.event_yellow
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    editingActivity: Activity? = null,
    initialDate: LocalDate,
    initialStartTime: LocalTime = LocalTime.of(12, 0),
    initialEndTime: LocalTime = LocalTime.of(13, 0),
    onDismiss: () -> Unit,
    onConfirm: (Activity) -> Unit,
    onDelete: (Int) -> Unit
) {
    val editingInterval = editingActivity?.timeInterval

    var title by remember { mutableStateOf(editingActivity?.title ?: "") }

    // Состояние для геопозиции
    var tempLocation by remember { mutableStateOf(editingActivity?.location) }
    var showMapSelection by remember { mutableStateOf(false) }

    val initialStartHour = editingInterval?.startTime?.let {
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault()).hour
    } ?: initialStartTime.hour

    val initialStartMinute = editingInterval?.startTime?.let {
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault()).minute
    } ?: initialStartTime.minute

    val initialEndHour = editingInterval?.endTime?.let {
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault()).hour
    } ?: initialEndTime.hour

    val initialEndMinute = editingInterval?.endTime?.let {
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(it), ZoneId.systemDefault()).minute
    } ?: initialEndTime.minute

    var startH by remember { mutableIntStateOf(initialStartHour) }
    var startM by remember { mutableIntStateOf(initialStartMinute) }
    var endH by remember { mutableIntStateOf(initialEndHour) }
    var endM by remember { mutableIntStateOf(initialEndMinute) }

    val defaultColor = colorResource(R.color.event_purple)
    var selectedColor by remember(editingActivity) {
        mutableStateOf(
            editingActivity?.color?.let { Color(it) } ?: defaultColor
        )
    }

    val defaultTitle = stringResource(R.string.default_activity_title)
    val okText = stringResource(R.string.ok)
    val cancelText = stringResource(R.string.cancel)
    val newActivityText = stringResource(R.string.new_activity)
    val editActivityText = stringResource(R.string.edit_activity)
    val titleLabel = stringResource(R.string.title)
    val startLabel = stringResource(R.string.start)
    val endLabel = stringResource(R.string.end)
    val selectLocationText = stringResource(R.string.select_location)
    val locationSelectedText = stringResource(R.string.location_selected)

    AlertDialog(
        onDismissRequest = {
            if (!showMapSelection) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (showMapSelection) selectLocationText
                    else if (editingActivity == null) newActivityText
                    else editActivityText
                )
                if (!showMapSelection && editingActivity != null) {
                    IconButton(onClick = { onDelete(editingActivity.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
                    }
                }
            }
        },
        text = {
            Crossfade(targetState = showMapSelection, label = "map_crossfade") { isMap ->
                if (isMap) {
                    // Карта выбора места
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            LocationPickerView(
                                initialLocation = tempLocation,
                                markerColor = selectedColor,
                                selectedPointTitle = stringResource(R.string.selected_point),
                                onLocationPicked = { location ->
                                    tempLocation = location
                                }
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { showMapSelection = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.apply_button))
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(titleLabel) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$startLabel: ${String.format("%02d:%02d", startH, startM)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                TimeWheelPicker(
                                    hour = startH,
                                    minute = startM,
                                    onTimeChange = { h, m -> startH = h; startM = m }
                                )

                                Spacer(Modifier.height(16.dp))

                                Text(
                                    text = "$endLabel: ${String.format("%02d:%02d", endH, endM)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                TimeWheelPicker(
                                    hour = endH,
                                    minute = endM,
                                    onTimeChange = { h, m -> endH = h; endM = m }
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { showMapSelection = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (tempLocation != null) locationSelectedText else selectLocationText
                            )
                        }

                        if (tempLocation != null) {
                            Spacer(Modifier.height(8.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = "${tempLocation?.geocodedAddress ?: "Выбранное место"}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = String.format("Координаты: %.4f, %.4f", tempLocation?.latitude ?: 0.0, tempLocation?.longitude ?: 0.0),
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = stringResource(R.string.event_color),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            EventColors.forEach { colorRes ->
                                val color = colorResource(colorRes)
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(color)
                                        .clickable { selectedColor = color }
                                        .then(
                                            if (selectedColor == color)
                                                Modifier.border(3.dp, Color.White, RoundedCornerShape(8.dp))
                                            else
                                                Modifier
                                        )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!showMapSelection) {
                Button(
                    onClick = {
                        val startDateTime = LocalDateTime.of(initialDate, LocalTime.of(startH.coerceIn(0, 23), startM.coerceIn(0, 59)))
                        val endDateTime = if (endH >= 24)
                            LocalDateTime.of(initialDate.plusDays(1), LocalTime.MIDNIGHT)
                        else
                            LocalDateTime.of(initialDate, LocalTime.of(endH.coerceIn(0, 23), endM.coerceIn(0, 59)))

                        val activity = Activity(
                            id = editingActivity?.id ?: 0,
                            userId = editingActivity?.userId ?: 0,
                            title = title.ifEmpty { defaultTitle },
                            timeInterval = TimeInterval(
                                startTime = startDateTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli(),
                                endTime = endDateTime.toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
                            ),
                            color = selectedColor.toArgb(),
                            location = tempLocation,
                            isSynced = false,
                            isDeleted = false,
                            lastTimeModified = System.currentTimeMillis()
                        )
                        onConfirm(activity)
                    }
                ) {
                    Text(okText)
                }
            }
        },
        dismissButton = {
            if (!showMapSelection) {
                TextButton(onClick = onDismiss) {
                    Text(cancelText)
                }
            }
        }
    )
}

@Composable
fun LocationPickerView(
    initialLocation: Location?,
    markerColor: Color,
    selectedPointTitle: String,
    onLocationPicked: (Location) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                val startPoint = if (initialLocation != null) {
                    GeoPoint(initialLocation.latitude, initialLocation.longitude)
                } else {
                    GeoPoint(55.7558, 37.6173)
                }
                controller.setCenter(startPoint)
                tileProvider.tileSource = TileSourceFactory.DEFAULT_TILE_SOURCE

                // Дефолтный маркер OSMDroid (красная булавка)
                val defaultIcon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)
                defaultIcon?.setTint(markerColor.toArgb())

                val marker = Marker(this).apply {
                    position = startPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = selectedPointTitle
                    icon = defaultIcon
                }

                if (initialLocation != null) overlays.add(marker)

                val receiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        overlays.filterIsInstance<Marker>()
                            .filter { it.title == selectedPointTitle }
                            .forEach { overlays.remove(it) }

                        val newMarker = Marker(this@apply).apply {
                            position = p
                            title = selectedPointTitle
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = defaultIcon
                        }
                        overlays.add(newMarker)
                        invalidate()
                        onLocationPicked(Location(p.latitude, p.longitude, null))
                        return true
                    }
                    override fun longPressHelper(p: GeoPoint): Boolean = false
                }
                overlays.add(MapEventsOverlay(receiver))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun TimeWheelPicker(
    hour: Int,
    minute: Int,
    onTimeChange: (Int, Int) -> Unit
) {
    val hourLabel = stringResource(R.string.hour_label)
    val minuteLabel = stringResource(R.string.minute_label)

    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(
            count = 24,
            initialValue = hour,
            label = hourLabel,
            onValueChange = { onTimeChange(it, minute) }
        )
        Spacer(Modifier.width(20.dp))
        WheelColumn(
            count = 60,
            initialValue = minute,
            label = minuteLabel,
            onValueChange = { onTimeChange(hour, it) }
        )
    }
}

@Composable
fun WheelColumn(
    count: Int,
    initialValue: Int,
    label: String,
    onValueChange: (Int) -> Unit
) {
    val minuteLabel = stringResource(R.string.minute_label)
    val isMinuteColumn = label == minuteLabel

    val itemHeight = 34.dp
    val totalItems = 10000
    val startIndex = (totalItems / 2) - ((totalItems / 2) % count) + initialValue
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex - 1)
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val currentSelected by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex + 1) % count
        }
    }

    LaunchedEffect(currentSelected) {
        onValueChange(currentSelected)
    }

    Box(
        modifier = Modifier.width(60.dp).height(itemHeight * 3),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(itemHeight)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
        )
        LazyColumn(
            state = listState,
            flingBehavior = snapBehavior,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(totalItems) { index ->
                val displayValue = index % count
                val isSelected = (listState.firstVisibleItemIndex + 1) == index

                Box(
                    modifier = Modifier.fillMaxWidth().height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isMinuteColumn)
                            String.format("%02d%s", displayValue, label)
                        else
                            "$displayValue$label",
                        fontSize = if (isSelected) 17.sp else 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                        modifier = Modifier.alpha(if (isSelected) 1f else 0.4f)
                    )
                }
            }
        }
    }
}