package com.example.havetime.domain.model

import java.time.LocalDateTime

data class TimeInterval(
    val id: String = java.util.UUID.randomUUID().toString(),
    val start: LocalDateTime,
    val end: LocalDateTime,
    val title: String,
    val color: Int
)