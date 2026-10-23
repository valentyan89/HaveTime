package com.example.havetime.presentation.common.navigation.bottom_bar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun BottomBarGlow(
    tabCount: Int,
    animatedSelectedTabIndex: Float,
    animatedColor: Color,
    isBlurSupported: Boolean
) {
    Canvas(
        modifier =
        Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .then(
                if (isBlurSupported) {
                    Modifier.blur(50.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                } else {
                    Modifier
                }
            )
    ) {
        val tabWidth = size.width / tabCount
        val centerOffset =
            Offset(
                x = (tabWidth * animatedSelectedTabIndex) + tabWidth / 2,
                y = size.height / 2
            )

        if (isBlurSupported) {
            drawCircle(
                color = animatedColor.copy(alpha = .6f),
                radius = size.height / 2,
                center = centerOffset
            )
        } else {
            val glowRadius = size.height * 0.9f
            drawCircle(
                brush =
                Brush.radialGradient(
                    colors =
                    listOf(
                        animatedColor.copy(alpha = 0.4f),
                        animatedColor.copy(alpha = 0.2f),
                        Color.Transparent
                    ),
                    center = centerOffset,
                    radius = glowRadius
                ),
                radius = glowRadius,
                center = centerOffset
            )
        }
    }
}
