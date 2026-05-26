package com.example.havetime.data.mapper

import com.example.havetime.data.local.entity.UserEntity
import com.example.havetime.data.remote.response.LoginResponse
import com.example.havetime.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = serverId,
        login = login,
        token = token,
        createdAt = createdAt
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        serverId = id,
        login = login,
        token = token,
        createdAt = createdAt,
        lastSyncAt = System.currentTimeMillis()
    )
}

fun LoginResponse.toDomain(): User {
    return User(
        id = id.toIntOrNull() ?: 0,
        login = login,
        token = token,
        createdAt = System.currentTimeMillis()
    )
}
