package com.example.havetime.data.mapper

import com.example.havetime.data.local.entity.ActivityEntity
import com.example.havetime.data.model.activity.TimeIntervalDto
import com.example.havetime.data.model.activity.ActivityDto
import com.example.havetime.data.model.activity.LocationDto
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
import com.example.havetime.domain.model.TimeInterval
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun ActivityEntity.toDomain(): Activity {
    return Activity(
        id = id,
        title = title,
        color = color,
        timeInterval = TimeInterval(
            startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInterval.startTime), ZoneId.systemDefault()),
            endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInterval.endTime), ZoneId.systemDefault())
        ),
        location = location?.let {
            Location(
                latitude = it.latitude,
                longitude = it.longitude,
                geocodedAddress = it.geocodedAddress
            )
        },
        offsetX = offsetX,
        widthPx = widthPx,
        paddingEnd = paddingEnd
    )
}

fun Activity.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        title = title,
        color = color,
        timeInterval = TimeIntervalDto(
            startTime = timeInterval.startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endTime = timeInterval.endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        ),
        location = location?.let {
            LocationDto(
                latitude = it.latitude,
                longitude = it.longitude,
                geocodedAddress = it.geocodedAddress
            )
        },
        offsetX = offsetX,
        widthPx = widthPx,
        paddingEnd = paddingEnd
    )
}

fun ActivityDto.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
        title = title,
        color = color,
        timeInterval = timeInterval,
        location = location
    )
}

fun ActivityDto.toDomain(): Activity {
    return Activity(
        id = id,
        title = title,
        color = color,
        timeInterval = TimeInterval(
            startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInterval.startTime), ZoneId.systemDefault()),
            endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timeInterval.endTime), ZoneId.systemDefault())
        ),
        location = location?.let {
            Location(
                latitude = it.latitude,
                longitude = it.longitude,
                geocodedAddress = it.geocodedAddress
            )
        }
    )
}

fun Activity.toDto(): ActivityDto {
    return ActivityDto(
        id = id,
        title = title,
        color = color,
        timeInterval = TimeIntervalDto(
            startTime = timeInterval.startTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            endTime = timeInterval.endTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        ),
        location = location?.let {
            LocationDto(
                latitude = it.latitude,
                longitude = it.longitude,
                geocodedAddress = it.geocodedAddress
            )
        }
    )
}
