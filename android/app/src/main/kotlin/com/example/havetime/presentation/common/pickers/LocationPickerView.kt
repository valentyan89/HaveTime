package com.example.havetime.presentation.common.pickers

import android.graphics.drawable.GradientDrawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.havetime.R
import com.example.havetime.domain.model.Location
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

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
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                val startPoint =
                    if (initialLocation != null) {
                        GeoPoint(initialLocation.latitude, initialLocation.longitude)
                    } else {
                        GeoPoint(55.7558, 37.6173)
                    }
                controller.setCenter(startPoint)
                tileProvider.tileSource = TileSourceFactory.DEFAULT_TILE_SOURCE

                val marker =
                    Marker(this).apply {
                        position = startPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = markerTitle

                        val drawable =
                            GradientDrawable().apply {
                                shape = GradientDrawable.OVAL
                                setColor(markerColor.toArgb())
                                setSize(60, 60)
                                setStroke(4, android.graphics.Color.WHITE)
                            }
                        icon = drawable
                    }
                if (initialLocation != null) overlays.add(marker)

                val receiver =
                    object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                            overlays.filterIsInstance<Marker>().forEach { overlays.remove(it) }
                            val newMarker =
                                Marker(this@apply).apply {
                                    position = p
                                    title = markerTitle
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                                    val drawable =
                                        GradientDrawable().apply {
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
