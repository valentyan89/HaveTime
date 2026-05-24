package com.example.havetime.domain.model

data class Activity(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val timeInterval: TimeInterval,
    val color: Int,
    val location: Location? = null,
    val offsetX: Float = 0f,
    val widthPx: Float? = null,
    val paddingEnd: Float = 16f
)