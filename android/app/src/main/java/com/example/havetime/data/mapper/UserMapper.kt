package com.example.havetime.data.mapper

import com.example.havetime.data.local.entity.UserEntity
import com.example.havetime.data.remote.response.LoginResponse
import com.example.havetime.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        login = login,
        email = email,
        token = token
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        login = login,
        email = email,
        token = token
    )
}

fun LoginResponse.toDomain(): User {
    return User(
        id = id,
        login = login,
        email = "", // Сервер может не возвращать email при логине
        token = token
    )
}