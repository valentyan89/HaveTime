package com.example.havetime.data.model.activity

import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import com.example.havetime.data.remote.serializer.LocalDateTimeSerializer

@Serializable
data class TimeIntervalDto(
    @Serializable(with = LocalDateTimeSerializer::class)
    val start: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val end: LocalDateTime
)