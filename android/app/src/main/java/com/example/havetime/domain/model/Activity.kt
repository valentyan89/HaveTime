package com.example.havetime.domain.model

data class Activity(
    val id: Int,
    val userId: Int,
    val title: String,
    val timeInterval: TimeInterval,
    val color: Int,
    val location: Location?,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val lastTimeModified: Long
)