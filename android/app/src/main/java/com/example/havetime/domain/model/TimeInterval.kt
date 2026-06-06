package com.example.havetime.domain.model

import java.time.LocalDateTime

data class TimeInterval(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)