package com.example.havetime.presentation.screens.day

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@Composable
fun AddActivityDialog(
    editingActivity: Activity? = null,
    allActivities: List<Activity> = emptyList(),
    initialDate: LocalDate,
    initialStartTime: LocalTime = LocalTime.of(12, 0),
    initialEndTime: LocalTime = LocalTime.of(13, 0),
    initialLocation: Location? = null,
    onDismiss: () -> Unit,
    onConfirm: (Activity) -> Unit,
    onDelete: (String) -> Unit
) {
    var title by remember(editingActivity?.id) { mutableStateOf(editingActivity?.title ?: "") }
    
    val dateFormatter = remember { DateTimeFormatter.ofPattern("d.M.yyyy") }
    var dateText by remember(editingActivity?.id, initialDate) { 
        mutableStateOf((editingActivity?.timeInterval?.start?.toLocalDate() ?: initialDate).format(dateFormatter)) 
    }

    var startH by remember(editingActivity?.id, initialStartTime) { mutableIntStateOf(editingActivity?.timeInterval?.start?.hour ?: initialStartTime.hour) }
    var startM by remember(editingActivity?.id, initialStartTime) { mutableIntStateOf(editingActivity?.timeInterval?.start?.minute ?: initialStartTime.minute) }
    var endH by remember(editingActivity?.id, initialEndTime) { mutableIntStateOf(editingActivity?.timeInterval?.end?.hour ?: initialEndTime.hour) }
    var endM by remember(editingActivity?.id, initialEndTime) { mutableIntStateOf(editingActivity?.timeInterval?.end?.minute ?: initialEndTime.minute) }
    
    var selectedColor by remember(editingActivity?.id, editingActivity?.color) { 
        mutableStateOf(Color(editingActivity?.color ?: 0xFF854CE5.toInt())) 
    }
    
    var showMapSelection by remember { mutableStateOf(false) }
    var tempLocation by remember(editingActivity?.id, initialLocation) { 
        mutableStateOf(initialLocation ?: editingActivity?.location) 
    }

    val calculatedEndDate = remember(dateText, startH, startM, endH, endM) {
        try {
            val startDate = LocalDate.parse(dateText, dateFormatter)
            val startTime = LocalTime.of(startH % 24, startM % 60)
            val endTime = LocalTime.of(endH % 24, endM % 60)
            var endDate = startDate
            if (endTime.isBefore(startTime) || endH >= 24) endDate = endDate.plusDays(1)
            endDate.format(dateFormatter)
        } catch (e: Exception) { dateText }
    }

    val colorRows = listOf(
        listOf(Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2), Color(0xFF5C6BC0)),
        listOf(Color(0xFF42A5F5), Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFFFFCA28), Color(0xFFFF7043))
    )

    val defaultActivityTitle = stringResource(R.string.dialog_default_activity_title)
    val mapSelectedPointTitle = stringResource(R.string.map_selected_point)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (showMapSelection) {
                    stringResource(R.string.map_title_selection)
                } else if (editingActivity == null) {
                    stringResource(R.string.dialog_new_activity)
                } else {
                    stringResource(R.string.dialog_edit_activity)
                }
            )
        },
        text = {
            Crossfade(targetState = showMapSelection) { isMap ->
                if (isMap) {
                    Column(Modifier.fillMaxWidth().height(450.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.weight(1f).padding(horizontal = 8.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                            LocationPickerView(
                                initialLocation = tempLocation,
                                markerColor = selectedColor,
                                otherActivities = allActivities.filter { it.id != editingActivity?.id },
                                selectedPointTitle = mapSelectedPointTitle,
                                onLocationPicked = { tempLocation = it }
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { showMapSelection = false }, 
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), 
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.dialog_apply_button))
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(rememberScrollState())) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(stringResource(R.string.dialog_title_label)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))
                        
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = dateText,
                                onValueChange = { input ->
                                    if (input.all { it.isDigit() || it == '.' }) dateText = input
                                },
                                label = { Text("Начало") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            
                            Spacer(Modifier.width(8.dp))
                            
                            OutlinedTextField(
                                value = calculatedEndDate,
                                onValueChange = {},
                                label = { Text("Конец") },
                                modifier = Modifier.weight(1f),
                                readOnly = true,
                                singleLine = true
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        
                        OutlinedButton(
                            onClick = { showMapSelection = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (tempLocation != null) {
                                    stringResource(R.string.dialog_location_selected)
                                } else {
                                    stringResource(R.string.dialog_select_location)
                                }
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        TimeWheelPicker(startH % 24, startM % 60) { h, m -> startH = h; startM = m }
                        Spacer(Modifier.height(12.dp))
                        TimeWheelPicker(endH % 24, endM % 60) { h, m -> endH = h; endM = m }

                        Spacer(Modifier.height(20.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            colorRows.forEach { row ->
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                                    row.forEach { colorItem ->
                                        Box(
                                            modifier = Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(colorItem)
                                                .clickable { selectedColor = colorItem }
                                                .padding(4.dp)
                                        ) {
                                            if (selectedColor == colorItem) {
                                                Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(4.dp)))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!showMapSelection) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (editingActivity != null) {
                        TextButton(
                            onClick = { onDelete(editingActivity.id) },
                        ) {
                            Text(stringResource(R.string.dialog_delete), color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Spacer(Modifier.width(1.dp))
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onDismiss) { 
                            Text(stringResource(R.string.dialog_cancel)) 
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val finalDate = try {
                                    LocalDate.parse(dateText, dateFormatter)
                                } catch (e: Exception) {
                                    LocalDate.now()
                                }

                                val start = LocalDateTime.of(finalDate, LocalTime.of(startH % 24, startM % 60))
                                var end = LocalDateTime.of(finalDate, LocalTime.of(endH % 24, endM % 60))

                                if (end.isBefore(start) || endH >= 24) {
                                    end = end.plusDays(1)
                                }
                                if (end.isBefore(start.plusMinutes(10))) end = start.plusMinutes(10)

                                val updatedActivity = (editingActivity ?: Activity(
                                    title = title.ifEmpty { defaultActivityTitle },
                                    timeInterval = TimeInterval(start, end),
                                    color = selectedColor.toArgb(),
                                )).copy(
                                    title = title.ifEmpty { defaultActivityTitle },
                                    timeInterval = TimeInterval(start, end),
                                    color = selectedColor.toArgb(),
                                    location = tempLocation
                                )
                                onConfirm(updatedActivity)
                            },
                            shape = RoundedCornerShape(24.dp)
                        ) { 
                            Text(stringResource(R.string.dialog_ok)) 
                        }
                    }
                }
            }
        },
        dismissButton = null
    )
}

@Composable
fun LocationPickerView(
    initialLocation: Location?,
    markerColor: Color,
    otherActivities: List<Activity>,
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

                otherActivities.filter { it.location != null }.forEach { act ->
                    val m = Marker(this).apply {
                        position = GeoPoint(act.location!!.latitude, act.location!!.longitude)
                        title = act.title
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        val icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)?.mutate()
                        icon?.setTint(act.color)
                        this.icon = icon
                    }
                    overlays.add(m)
                }

                val marker = Marker(this).apply {
                    position = startPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = selectedPointTitle
                    val icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)?.mutate()
                    icon?.setTint(markerColor.toArgb())
                    this.icon = icon
                }
                if (initialLocation != null) overlays.add(marker)

                val receiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        overlays.filterIsInstance<Marker>().filter { it.title == selectedPointTitle }.forEach { overlays.remove(it) }
                        val newMarker = Marker(this@apply).apply {
                            position = p
                            title = selectedPointTitle
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            val icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)?.mutate()
                            icon?.setTint(markerColor.toArgb())
                            this.icon = icon
                        }
                        overlays.add(newMarker)
                        invalidate()
                        onLocationPicked(Location(p.latitude, p.longitude))
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
fun TimeWheelPicker(hour: Int, minute: Int, onTimeChange: (Int, Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(24, hour, "") { onTimeChange(it, minute) }
        Spacer(Modifier.width(20.dp))
        WheelColumn(60, minute, "") { onTimeChange(hour, it) }
    }
}

@Composable
fun WheelColumn(count: Int, initialValue: Int, label: String, onValueChange: (Int) -> Unit) {
    val itemHeight = 34.dp
    val totalItems = 10000
    val startIndex = (totalItems / 2) - ((totalItems / 2) % count) + initialValue
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (startIndex - 1).coerceAtLeast(0))
    val snapBehavior = rememberSnapFlingBehavior(listState)

    val currentSelected by remember { derivedStateOf { (listState.firstVisibleItemIndex + 1) % count } }

    LaunchedEffect(currentSelected) { onValueChange(currentSelected) }

    Box(Modifier.width(65.dp).height(itemHeight * 3), Alignment.Center) {
        Box(modifier = Modifier.fillMaxWidth().height(itemHeight).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp)))
        LazyColumn(state = listState, flingBehavior = snapBehavior, modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            items(totalItems) { index ->
                val displayValue = index % count
                val isSelected = (listState.firstVisibleItemIndex + 1) == index
                Box(Modifier.fillMaxWidth().height(itemHeight), Alignment.Center) {
                    Text(
                        text = String.format(Locale.getDefault(), "%02d%s", displayValue, label),
                        fontSize = if (isSelected) 18.sp else 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
