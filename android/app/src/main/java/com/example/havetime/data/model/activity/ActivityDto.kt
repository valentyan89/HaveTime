package com.example.havetime.data.model.activity

import kotlinx.serialization.Serializable

@Serializable
data class ActivityDto(
    val id: Int,
    val userId: Int,
    val title: String,
    val timeInterval: TimeIntervalDto,
    val color: Int,
    val location: LocationDto?,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val lastTimeModified: Long = System.currentTimeMillis()
)