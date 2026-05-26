package com.example.havetime.domain.model

data class Activity(
    val id: Int = 0,
    val title: String,
    val timeInterval: TimeInterval,
    val color: Int,
    val location: Location? = null,
    val offsetX: Float = 0f,
    val widthPx: Float? = null,
    val paddingEnd: Float = 16f
)
