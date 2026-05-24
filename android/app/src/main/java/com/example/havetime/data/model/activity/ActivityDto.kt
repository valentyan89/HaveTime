package com.example.havetime.data.model.activity

data class ActivityDto(
    val id: String?,
    val title: String,
    val timeInterval: TimeIntervalDto,
    val color: Int,
    val location: LocationDto?
)