package com.example.havetime.domain.model

data class Activity(
    val id: Int,
    val title: String,
    val timeInterval: TimeInterval,
    val color: Int,
    val location: Location?
)