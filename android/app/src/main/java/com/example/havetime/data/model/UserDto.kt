package com.example.havetime.data.model

data class UserDto(
    val id: Int,
    val login: String,
    val token: String,
    val createdAt: Long
)