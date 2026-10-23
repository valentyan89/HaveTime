package com.example.havetime.presentation.common.navigation.bottom_bar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
internal fun BottomBarOutline(
    tabCount: Int,
    animatedSelectedTabIndex: Float,
    animatedColor: Color
) {
    Canvas(
        modifier =
        Modifier
            .fillMaxSize()
            .clip(CircleShape)
    ) {
        val path =
            Path().apply {
                addRoundRect(RoundRect(size.toRect(), CornerRadius(size.height)))
            }
        val length = PathMeasure().apply { setPath(path, false) }.length

        val tabWidth = size.width / tabCount
        drawPath(
            path,
            brush =
            Brush.horizontalGradient(
                colors =
                listOf(
                    animatedColor.copy(alpha = 0f),
                    animatedColor.copy(alpha = 1f),
                    animatedColor.copy(alpha = 1f),
                    animatedColor.copy(alpha = 0f)
                ),
                startX = tabWidth * animatedSelectedTabIndex,
                endX = tabWidth * (animatedSelectedTabIndex + 1)
            ),
            style =
            Stroke(
                width = 6f,
                pathEffect =
                PathEffect.dashPathEffect(
                    intervals = floatArrayOf(length / 2, length)
                )
            )
        )
    }
}
