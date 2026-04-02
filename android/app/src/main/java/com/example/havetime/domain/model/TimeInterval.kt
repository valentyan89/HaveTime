package com.example.havetime.domain.model

import android.icu.util.Calendar
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class TimeInterval(
    val id: String = java.util.UUID.randomUUID().toString(),
    val start: Calendar,
    val end: Calendar,
    val title: String,
    val color: Int = Color(0xFF6200EE).toArgb(),
    val intensity: Int = 0
)