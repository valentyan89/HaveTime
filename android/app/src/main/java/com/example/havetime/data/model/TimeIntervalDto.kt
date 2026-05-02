package com.example.havetime.data.model

import java.time.LocalDateTime

data class TimeIntervalDto(
    val start: LocalDateTime,
    val end: LocalDateTime
)