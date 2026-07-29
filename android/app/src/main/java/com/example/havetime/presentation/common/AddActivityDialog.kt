package com.example.havetime.presentation.common

import androidx.compose.ui.graphics.lerp
import android.graphics.drawable.GradientDrawable
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityDialog(
    editingActivity: Activity? = null,
    initialDate: LocalDate,
    initialStartTime: LocalTime,
    initialEndTime: LocalTime,
    initialLocation: Location? = null,
    onDismiss: () -> Unit,
    onConfirm: (Activity) -> Unit,
    onDelete: (Int) -> Unit
) {
    var title by remember(editingActivity?.id) { mutableStateOf(editingActivity?.title ?: "") }

    val currentLocale = Locale.getDefault()
    val dateFormatter = remember(currentLocale) {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(currentLocale)
    }

    val startDT = if (editingActivity != null) {
        Instant.ofEpochMilli(editingActivity.timeInterval.startTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
    } else {
        LocalDateTime.of(initialDate, initialStartTime)
    }

    val endDT = if (editingActivity != null) {
        Instant.ofEpochMilli(editingActivity.timeInterval.endTime).atZone(ZoneId.systemDefault()).toLocalDateTime()
    } else {
        LocalDateTime.of(initialDate, initialEndTime)
    }

    var startDate by remember(editingActivity?.id) { mutableStateOf(startDT.toLocalDate()) }
    var startH by remember(editingActivity?.id) { mutableIntStateOf(startDT.hour) }
    var startM by remember(editingActivity?.id) { mutableIntStateOf(startDT.minute) }

    var endDate by remember(editingActivity?.id) { mutableStateOf(endDT.toLocalDate()) }
    var endH by remember(editingActivity?.id) { mutableIntStateOf(endDT.hour) }
    var endM by remember(editingActivity?.id) { mutableIntStateOf(endDT.minute) }

    var selectedColor by remember(editingActivity?.id, editingActivity?.color) {
        mutableStateOf(Color(editingActivity?.color ?: 0xFF854CE5.toInt()))
    }

    var showMapSelection by remember { mutableStateOf(false) }
    var tempLocation by remember(editingActivity?.id, initialLocation) {
        mutableStateOf(initialLocation ?: editingActivity?.location)
    }

    LaunchedEffect(startH, startM, endH, endM) {
        val st = LocalTime.of(startH % 24, startM % 60)
        val et = LocalTime.of(endH % 24, endM % 60)
        if (et.isBefore(st) || endH >= 24) {
            if (endDate == startDate) {
                endDate = startDate.plusDays(1)
            }
        } else if (endDate == startDate.plusDays(1) && endH < 24) {
            endDate = startDate
        }
    }

    val filterColor = MaterialTheme.colorScheme.primary
    val filterIntensity = 0.2f

    val colorRows = listOf(
        listOf(Color(0xFFEF5350), Color(0xFFEC407A), Color(0xFFAB47BC), Color(0xFF7E57C2), Color(0xFF5C6BC0)),
        listOf(Color(0xFF42A5F5), Color(0xFF26A69A), Color(0xFF66BB6A), Color(0xFFFFCA28), Color(0xFFFF7043))
    ).map { row ->
        row.map { color ->
            lerp(color, filterColor, filterIntensity)
        }
    }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        startDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (endDate.isBefore(startDate)) endDate = startDate
                    }
                    showStartDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        endDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        if (endDate.isBefore(startDate)) startDate = endDate
                    }
                    showEndDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            }
        ) { DatePicker(state = datePickerState) }
    }

    val defaultActivityTitle = stringResource(R.string.default_activity_title)

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.95f),
        title = {
            Text(
                text = if (showMapSelection) stringResource(R.string.select_location)
                else if (editingActivity == null) stringResource(R.string.new_activity)
                else stringResource(R.string.edit_activity),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Default
            )
        },
        text = {
            Crossfade(targetState = showMapSelection, label = "") { isMap ->
                if (isMap) {
                    Column(Modifier.fillMaxWidth().height(450.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.weight(1f).padding(horizontal = 8.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                            LocationPickerView(
                                initialLocation = tempLocation,
                                markerColor = selectedColor,
                                onLocationPicked = { tempLocation = it }
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { showMapSelection = false },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(stringResource(R.string.apply_button))
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(rememberScrollState())) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text(stringResource(R.string.title)) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.start), style = MaterialTheme.typography.labelMedium)
                                TextButton(onClick = { showStartDatePicker = true }, contentPadding = PaddingValues(0.dp)) {
                                    Text(startDate.format(dateFormatter))
                                }
                            }
                            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text(stringResource(R.string.end), style = MaterialTheme.typography.labelMedium)
                                TextButton(onClick = { showEndDatePicker = true }, contentPadding = PaddingValues(0.dp)) {
                                    Text(endDate.format(dateFormatter))
                                }
                            }
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
                                text = if (tempLocation != null) stringResource(R.string.location_selected) else stringResource(R.string.select_location)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "${stringResource(R.string.start_time)}: ${String.format(currentLocale, "%02d:%02d", startH % 24, startM % 60)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                        TimeWheelPicker(startH % 24, startM % 60) { h, m -> startH = h; startM = m }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "${stringResource(R.string.end_time)}: ${String.format(currentLocale, "%02d:%02d", endH % 24, endM % 60)}",
                            style = MaterialTheme.typography.labelSmall
                        )
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (editingActivity != null) {
                            TextButton(onClick = { onDelete(editingActivity.id) }) {
                                Text(stringResource(R.string.delete), color = Color.Red, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(8.dp))
                        }
                        TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                    }

                    Button(
                        onClick = {
                            val st = LocalDateTime.of(startDate, LocalTime.of(startH % 24, startM % 60))
                            val et = LocalDateTime.of(endDate, LocalTime.of(endH % 24, endM % 60))

                            val updatedActivity = Activity(
                                id = editingActivity?.id ?: 0,
                                userId = editingActivity?.userId ?: 1,
                                title = title.ifEmpty { defaultActivityTitle },
                                timeInterval = TimeInterval(
                                    st.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                                    et.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                ),
                                color = selectedColor.toArgb(),
                                location = tempLocation,
                                lastTimeModified = System.currentTimeMillis()
                            )
                            onConfirm(updatedActivity)
                        },
                        shape = RoundedCornerShape(24.dp)
                    ) { Text(stringResource(R.string.ok)) }
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
    onLocationPicked: (Location) -> Unit
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val markerTitle = stringResource(R.string.select_location)

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

                val marker = Marker(this).apply {
                    position = startPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = markerTitle

                    val drawable = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(markerColor.toArgb())
                        setSize(60, 60)
                        setStroke(4, android.graphics.Color.WHITE)
                    }
                    icon = drawable
                }
                if (initialLocation != null) overlays.add(marker)

                val receiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        overlays.filterIsInstance<Marker>().forEach { overlays.remove(it) }
                        val newMarker = Marker(this@apply).apply {
                            position = p
                            title = markerTitle
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                            val drawable = GradientDrawable().apply {
                                shape = GradientDrawable.OVAL
                                setColor(markerColor.toArgb())
                                setSize(60, 60)
                                setStroke(4, android.graphics.Color.WHITE)
                            }
                            icon = drawable
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
fun TimeWheelPicker(hour: Int, minute: Int, onTimeChange: (Int, Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WheelColumn(24, hour, stringResource(R.string.hour_label)) { onTimeChange(it, minute) }
        Spacer(Modifier.width(20.dp))
        WheelColumn(60, minute, stringResource(R.string.minute_label)) { onTimeChange(hour, it) }
    }
}

@Composable
fun WheelColumn(count: Int, initialValue: Int, label: String, onValueChange: (Int) -> Unit) {
    val itemHeight = 34.dp
    val totalItems = 10000
    val startIndex = (totalItems / 2) - ((totalItems / 2) % count) + initialValue
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = (startIndex - 1).coerceAtLeast(0))
    val snapBehavior = rememberSnapFlingBehavior(listState)
    val currentLocale = Locale.getDefault()

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
                        text = String.format(currentLocale, "%02d%s", displayValue, label),
                        fontSize = if (isSelected) 18.sp else 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}