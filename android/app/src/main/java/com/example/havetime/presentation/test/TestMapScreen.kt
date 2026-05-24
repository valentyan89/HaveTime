package com.example.havetime.presentation.test

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.presentation.CalendarViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMapScreen(
    viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
) {
    val context = LocalContext.current

    var selectedGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }

    val mapView = remember { MapView(context) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Выберите место") })
        },
        floatingActionButton = {
            if (selectedGeoPoint != null) {
                ExtendedFloatingActionButton(
                    text = { Text("Создать с локацией") },
                    icon = { Icon(Icons.Default.Check, null) },
                    onClick = {
                        val activityWithLocation = Activity(
                            id = java.util.UUID.randomUUID().toString(),
                            title = "Задача на карте",
                            timeInterval = TimeInterval(
                                start = LocalDateTime.now(),
                                end = LocalDateTime.now().plusHours(1),
                            ),
                            color = 200,
                            location = Location(
                                latitude = selectedGeoPoint!!.latitude,
                                longitude = selectedGeoPoint!!.longitude,
                                geocodedAddress = "Координаты: ${selectedGeoPoint!!.latitude}"
                            )
                        )
                        viewModel.addActivity(activityWithLocation)
                        selectedGeoPoint = null
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            AndroidView(
                factory = {
                    mapView.apply {
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(GeoPoint(55.7558, 37.6173))

                        val receiver = object : MapEventsReceiver {
                            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                                selectedGeoPoint = p
                                overlays.filterIsInstance<Marker>().forEach { overlays.remove(it) }
                                val marker = Marker(this@apply).apply {
                                    position = p
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Выбранная точка"
                                }
                                overlays.add(marker)
                                invalidate()
                                return true
                            }

                            override fun longPressHelper(p: GeoPoint): Boolean = false
                        }
                        overlays.add(MapEventsOverlay(receiver))
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (selectedGeoPoint == null) {
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        "Нажмите на карту, чтобы выбрать место",
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}