package com.example.havetime.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.havetime.data.model.activity.LocationDto
import com.example.havetime.data.model.activity.TimeIntervalDto

@Entity(tableName = "activity")
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val title: String,
    val color: Int,
    @Embedded val timeInterval: TimeIntervalDto,
    @Embedded val location: LocationDto? = null,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val lastTimeModified: Long = System.currentTimeMillis()
)