package com.example.havetime.presentation.common.utils

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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