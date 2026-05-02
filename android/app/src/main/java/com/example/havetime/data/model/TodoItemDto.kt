package com.example.havetime.data.model

data class TodoItemDto(
    val id: Int,
    val title: String,
    val timeInterval: TimeIntervalDto,
    val color: Int
)