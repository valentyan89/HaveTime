package com.example.havetime.domain.model

data class TodoItem(
    val id: Int,
    val title: String,
    val timeInterval: TimeInterval,
    val color: Int
)