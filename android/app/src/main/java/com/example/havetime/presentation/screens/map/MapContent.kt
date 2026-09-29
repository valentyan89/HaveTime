package com.example.havetime.presentation.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.havetime.R
import com.example.havetime.domain.model.Activity
import com.example.havetime.presentation.common.utils.HaveText
import com.example.havetime.presentation.common.utils.rememberScreenBottomCornerRadius
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView

@Composable
fun MapContent(
    mapView: MapView,
    geoMarks: List<Activity>,
    paddingValues: PaddingValues
) {
    val screenCornerRadius = rememberScreenBottomCornerRadius()

    val cardShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = screenCornerRadius,
        bottomEnd = screenCornerRadius
    )

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 8.dp
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(cardShape)
        ) {

            AndroidView(
                factory = {
                    mapView.apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        zoomController.setVisibility(
                            CustomZoomButtonsController.Visibility.ALWAYS
                        )

                        controller.setZoom(15.0)
                        controller.setCenter(
                            GeoPoint(
                                55.7558,
                                37.6173
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            if (geoMarks.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                        .padding(
                            bottom = paddingValues.calculateBottomPadding() + 4.dp
                        ),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    HaveText(
                        modifier = Modifier.padding(12.dp),
                        text = stringResource(
                            R.string.no_location_events
                        ),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}