package com.example.havetime.data.model.activity

import java.time.LocalDateTime

data class TimeIntervalDto(
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)