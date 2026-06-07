package com.example.havetime.data.mapper

import com.example.havetime.data.local.entity.UserEntity
import com.example.havetime.data.model.UserDto
import com.example.havetime.domain.model.User
import java.time.LocalDateTime

fun UserEntity.toDomain(): User {
    return User(
        id = serverId,
        login = login,
        token = token,
        createdAt = createdAt
    )
}

fun UserDto.toEntity(): UserEntity {
    return UserEntity(
        serverId = id,
        login = login,
        token = token,
        createdAt = createdAt,
        lastSyncAt = System.currentTimeMillis()
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

fun UserEntity.toDto(): UserDto {
    return UserDto(
        id = serverId,
        login = login,
        token = token,
        createdAt = createdAt
    )
}