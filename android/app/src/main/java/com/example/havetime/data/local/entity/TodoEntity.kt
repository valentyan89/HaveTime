package com.example.havetime.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.havetime.data.model.TimeIntervalDto

@Entity(tableName = "todo")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val color: Int,
    @Embedded val timeInterval: TimeIntervalDto
)