package com.example.havetime.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.havetime.data.model.activity.LocationDto
import com.example.havetime.data.model.activity.TimeIntervalDto

@Entity(tableName = "activity")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val color: Int,
    @Embedded val timeInterval: TimeIntervalDto,
    @Embedded val location: LocationDto? = null,
    val offsetX: Float = 0f,
    val widthPx: Float? = null,
    val paddingEnd: Float = 16f
)
