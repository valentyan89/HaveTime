package com.example.havetime.domain.model

import android.icu.util.Calendar

data class TimeInterval(
    val id: String = java.util.UUID.randomUUID().toString(),
    val start: Calendar,
    val end: Calendar,
    val title: String = "Занятость",
    val intensity: Int = 0 // 0-100 для цвета
)