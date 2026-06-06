package com.example.havetime.domain.model

import java.time.LocalDateTime

data class TimeInterval(
    val start: LocalDateTime,
    val end: LocalDateTime
)