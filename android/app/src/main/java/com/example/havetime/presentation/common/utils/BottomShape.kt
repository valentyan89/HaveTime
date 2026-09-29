package com.example.havetime.presentation.common.utils

import android.os.Build
import android.view.RoundedCorner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat

@Composable
fun rememberScreenBottomCornerRadius(): Dp {
    val view = LocalView.current
    val density = LocalDensity.current
    var cornerRadius by remember { mutableStateOf(0.dp) }

    DisposableEffect(view) {
        fun updateCornerRadius() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val insets = view.rootWindowInsets

                if (insets != null) {
                    val leftRadius =
                        insets.getRoundedCorner(
                            RoundedCorner.POSITION_BOTTOM_LEFT
                        )?.radius ?: 0

                    val rightRadius =
                        insets.getRoundedCorner(
                            RoundedCorner.POSITION_BOTTOM_RIGHT
                        )?.radius ?: 0

                    val maxRadius = maxOf(leftRadius, rightRadius)

                    cornerRadius = with(density) {
                        maxRadius.toDp()
                    }
                }
            }
        }

        updateCornerRadius()

        ViewCompat.setOnApplyWindowInsetsListener(
            view
        ) { _, insets ->
            updateCornerRadius()
            insets
        }

        onDispose {
            ViewCompat.setOnApplyWindowInsetsListener(view, null)
        }
    }

    return cornerRadius
}