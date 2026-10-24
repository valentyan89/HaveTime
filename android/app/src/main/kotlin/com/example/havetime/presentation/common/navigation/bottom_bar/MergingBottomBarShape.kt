package com.example.havetime.presentation.common.navigation.bottom_bar

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


private val ACTION_SIZE = 62.dp
private val ACTION_SLOT_SIZE = 64.dp

// 64 dp под FAB + 12 dp зазора.
private val ACTION_RESERVED_WIDTH = 76.dp
private val ACTION_MERGE_TRAVEL = 32.dp

fun mergeTravelFraction(progress: Float): Float = 4f * progress * (1f - progress)


class MergingBottomBarShape(
    private val actionFraction: Float,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val fraction = actionFraction.coerceIn(0f, 1f)
        val progress = 1f - fraction

        val reservedWidth = with(density) {
            ACTION_RESERVED_WIDTH.toPx()
        }

        val actionDiameter = with(density) {
            ACTION_SIZE.toPx()
        }

        val actionSlotSize = with(density) {
            ACTION_SLOT_SIZE.toPx()
        }

        val maxTravel = with(density) {
            ACTION_MERGE_TRAVEL.toPx()
        }

        val barWidth = (
                size.width - reservedWidth * fraction
                ).coerceAtLeast(0f)

        val radius = size.height / 2f

        val barPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        left = 0f,
                        top = 0f,
                        right = barWidth,
                        bottom = size.height,
                    ),
                    cornerRadius = CornerRadius(radius, radius),
                )
            )
        }

        if (fraction <= 0f) {
            return Outline.Generic(barPath)
        }

        val travel = maxTravel * mergeTravelFraction(progress)

        val horizontalInset = (actionSlotSize - actionDiameter) / 2f
        val actionRight = size.width - horizontalInset - travel
        val actionTop = (size.height - actionDiameter) / 2f

        val actionPath = Path().apply {
            addOval(
                Rect(
                    left = actionRight - actionDiameter,
                    top = actionTop,
                    right = actionRight,
                    bottom = actionTop + actionDiameter,
                )
            )
        }

        return Outline.Generic(
            Path.combine(
                operation = PathOperation.Union,
                path1 = barPath,
                path2 = actionPath,
            )
        )
    }
}