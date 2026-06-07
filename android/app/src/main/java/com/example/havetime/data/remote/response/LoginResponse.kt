package com.example.havetime.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val id: Int,
    val login: String,
    val token: String
)
