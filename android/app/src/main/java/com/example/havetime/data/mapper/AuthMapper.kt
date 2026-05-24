package com.example.havetime.data.mapper

import com.example.havetime.data.remote.response.LoginResponse
import com.example.havetime.domain.model.User

fun LoginResponse.toDomain(): User {
    return User(
        id = id,
        login = login,
        token = token,
        createdAt = System.currentTimeMillis()
    )
}