package com.example.havetime.data.model.activity

import kotlinx.serialization.Serializable

@Serializable
data class TimeIntervalDto(
    val startTime: Long,
    val endTime: Long
)