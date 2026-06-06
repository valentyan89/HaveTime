package com.example.havetime.presentation.test

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.havetime.R
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
import com.example.havetime.presentation.CalendarViewModel
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestMapScreen(
    viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory),
    isSelectionMode: Boolean = false,
    onLocationSelected: (Location) -> Unit = {},
    onCreateActivityAtLocation: (Location) -> Unit = {},
    onActivityClick: (Activity) -> Unit = {}
) {
    val context = LocalContext.current
    val activities by viewModel.allActivities.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var selectedGeoPoint by remember { mutableStateOf<GeoPoint?>(null) }
    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        floatingActionButton = {
            if (isSelectionMode && selectedGeoPoint != null) {
                ExtendedFloatingActionButton(
                    text = { Text(stringResource(R.string.map_select_this_place)) },
                    icon = { Icon(Icons.Default.Check, null) },
                    onClick = {
                        onLocationSelected(Location(selectedGeoPoint!!.latitude, selectedGeoPoint!!.longitude))
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
                        setBuiltInZoomControls(false) 
                        controller.setZoom(12.0)
                        controller.setCenter(GeoPoint(55.7558, 37.6173))
                        tileProvider.tileSource = TileSourceFactory.DEFAULT_TILE_SOURCE
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    view.overlays.clear()
                    activities.filter { it.location != null && it.timeInterval.start.toLocalDate() == selectedDate }.forEach { activity ->
                        val marker = Marker(view).apply {
                            position = GeoPoint(activity.location!!.latitude, activity.location!!.longitude)
                            title = activity.title
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.marker_default)?.mutate()?.apply {
                                setTint(activity.color)
                            }
                            setOnMarkerClickListener { _, _ ->
                                onActivityClick(activity)
                                true
                            }
                        }
                        view.overlays.add(marker)
                    }

                    val receiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            if (isSelectionMode) {
                                selectedGeoPoint = p
                                view.overlays.filterIsInstance<Marker>().filter { it.title == context.getString(R.string.map_selected_point) }.forEach { view.overlays.remove(it) }
                                val marker = Marker(view).apply {
                                    position = p
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = context.getString(R.string.map_selected_point)
                                }
                                view.overlays.add(marker)
                            } else {
                                onCreateActivityAtLocation(Location(p.latitude, p.longitude))
                            }
                            view.invalidate()
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint): Boolean = false
                    }
                    view.overlays.add(MapEventsOverlay(receiver))
                    view.invalidate()
                }
            )

            Column(
                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 100.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.6f)).clickable { mapView.controller.zoomIn() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black.copy(alpha = 0.7f))
                }
                Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.6f)).clickable { mapView.controller.zoomOut() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Remove, contentDescription = null, tint = Color.Black.copy(alpha = 0.7f))
                }
            }
        }
    }
}
