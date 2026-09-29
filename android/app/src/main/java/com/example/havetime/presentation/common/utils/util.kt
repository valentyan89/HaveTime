package com.example.havetime.presentation.common.utils

import android.os.Build
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp

fun Modifier.legacyBlur(
    radius: Dp,
    edgeTreatment: BlurredEdgeTreatment = BlurredEdgeTreatment.Rectangle
): Modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    this.blur(radius, edgeTreatment)
} else {
    this
}