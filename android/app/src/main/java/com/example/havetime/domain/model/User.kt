package com.example.havetime.domain.model

data class User(
    val id: Int,
    val login: String,
    val token: String,
    val createdAt: Long
)
