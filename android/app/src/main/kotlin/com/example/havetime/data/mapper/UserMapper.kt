package com.example.havetime.data.mapper

import com.example.havetime.data.local.entity.UserEntity
import com.example.havetime.data.model.auth.UserDto
import com.example.havetime.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = serverId,
    login = login,
    token = token,
    createdAt = createdAt
)

fun UserDto.toEntity(): UserEntity = UserEntity(
    serverId = id,
    login = login,
    token = token,
    createdAt = createdAt,
    lastSyncAt = System.currentTimeMillis()
)

fun User.toEntity(): UserEntity = UserEntity(
    serverId = id,
    login = login,
    token = token,
    createdAt = createdAt,
    lastSyncAt = System.currentTimeMillis()
)

fun UserEntity.toDto(): UserDto = UserDto(
    id = serverId,
    login = login,
    token = token,
    createdAt = createdAt
)
