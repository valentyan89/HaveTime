package com.example.havetime.presentation.common.utils

import android.os.Build
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.navigationBottomPadding(): Modifier {
    val navigationBarsPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()

    val bottomPadding = if (navigationBarsPadding > 30.dp) {
        navigationBarsPadding
    } else {
        0.dp
    }

    return this.padding(bottom = bottomPadding)
}

fun Modifier.legacyBlur(
    radius: Dp,
    edgeTreatment: BlurredEdgeTreatment = BlurredEdgeTreatment.Rectangle
): Modifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    this.blur(radius, edgeTreatment)
} else {
    this
}
