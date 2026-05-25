package com.example.havetime.domain.model

data class User(
    val id: String,
    val login: String,
    val email: String,
    val token: String? = null
)