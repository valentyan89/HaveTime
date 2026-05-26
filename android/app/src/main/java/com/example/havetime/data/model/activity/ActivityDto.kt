package com.example.havetime.data.model.activity

import kotlinx.serialization.Serializable

@Serializable
data class ActivityDto(
    val id: Int,
    val title: String,
    val timeInterval: TimeIntervalDto,
    val color: Int,
    val location: LocationDto?
)
