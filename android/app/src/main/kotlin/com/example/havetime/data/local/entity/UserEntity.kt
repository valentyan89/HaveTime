package com.example.havetime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val serverId: Int,
    val login: String,
    val token: String,
    val createdAt: Long,
    val lastSyncAt: Long
)
