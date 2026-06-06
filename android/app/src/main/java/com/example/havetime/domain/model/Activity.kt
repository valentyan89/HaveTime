package com.example.havetime.domain.model

import java.util.UUID

data class Activity(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val timeInterval: TimeInterval,
    val color: Int,
    val location: Location? = null,
    val offsetX: Float = 0f,
    val widthPx: Float? = null,
    val paddingEnd: Float = 16f
)