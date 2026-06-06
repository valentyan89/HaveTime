package com.example.havetime.data.mapper

import com.example.havetime.data.local.entity.ActivityEntity
import com.example.havetime.data.model.activity.TimeIntervalDto
import com.example.havetime.data.model.activity.ActivityDto
import com.example.havetime.data.model.activity.ActivityNetworkDto
import com.example.havetime.data.model.activity.LocationDto
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
import com.example.havetime.domain.model.TimeInterval

fun ActivityDto.toNetworkDto(): ActivityNetworkDto {
    return ActivityNetworkDto(
        id = this.id,
        userId = this.userId,
        title = this.title,
        color = this.color,
        startTime = this.timeInterval.startTime,
        endTime = this.timeInterval.endTime,
        lat = this.location?.latitude,
        lon = this.location?.longitude,
        address = this.location?.geocodedAddress,
        updatedAt = this.lastTimeModified,
        isDeleted = this.isDeleted
    )
}

fun ActivityNetworkDto.toClientDto(): ActivityDto {
    val clientLocation =
        if (this.lat != null && this.lon != null) {
            LocationDto(
                latitude = this.lat,
                longitude = this.lon,
                geocodedAddress = this.address
            )
        } else {
            null
        }

    return ActivityDto(
        id = this.id,
        userId = this.userId,
        title = this.title,
        color = this.color,
        timeInterval = TimeIntervalDto(
            startTime = this.startTime,
            endTime = this.endTime
        ),
        location = clientLocation,
        isSynced = true,
        isDeleted = this.isDeleted,
        lastTimeModified = this.updatedAt
    )
}

fun ActivityEntity.toDomain(): Activity {
    return Activity(
        id = id,
        userId = userId,
        title = title,
        color = color,
        timeInterval = TimeInterval(
            startTime = timeInterval.startTime,
            endTime = timeInterval.endTime
        ),
        location = location?.let {
            Location(
                latitude = location.latitude,
                longitude = location.longitude,
                geocodedAddress = location.geocodedAddress
            )
        },
        isSynced = isSynced,
        isDeleted = isDeleted,
        lastTimeModified = lastTimeModified
    )
}

fun Activity.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        userId = userId,
        title = title,
        color = color,
        timeInterval = TimeIntervalDto(
            startTime = timeInterval.startTime,
            endTime = timeInterval.endTime
        ),
        location = location?.let {
            LocationDto(
                latitude = location.latitude,
                longitude = location.longitude,
                geocodedAddress = location.geocodedAddress
            )
        },
        isSynced = isSynced,
        isDeleted = isDeleted,
        lastTimeModified = lastTimeModified
    )
}

fun ActivityDto.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        userId = userId,
        title = title,
        color = color,
        timeInterval = timeInterval,
        location = location,
        isSynced = true,
        isDeleted = false,
        lastTimeModified = System.currentTimeMillis()
    )
}

fun ActivityDto.toDomain(): Activity {
    return Activity(
        id = id,
        userId = userId,
        title = title,
        color = color,
        timeInterval = TimeInterval(
            startTime = timeInterval.startTime,
            endTime = timeInterval.endTime
        ),
        location = location?.let {
            Location(
                latitude = it.latitude,
                longitude = it.longitude,
                geocodedAddress = it.geocodedAddress
            )
        },
        isSynced = true,
        isDeleted = false,
        lastTimeModified = lastTimeModified
    )
}

fun Activity.toDto(): ActivityDto {
    return ActivityDto(
        id = id,
        userId = userId,
        title = title,
        color = color,
        timeInterval = TimeIntervalDto(
            startTime = timeInterval.startTime,
            endTime = timeInterval.endTime
        ),
        location = this.location?.let {
            LocationDto(
                latitude = it.latitude,
                longitude = it.longitude,
                geocodedAddress = it.geocodedAddress
            )
        },
        isSynced = isSynced,
        isDeleted = isDeleted,
        lastTimeModified = lastTimeModified
    )
}