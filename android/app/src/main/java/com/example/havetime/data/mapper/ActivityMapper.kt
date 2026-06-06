package com.example.havetime.data.mapper

import com.example.havetime.data.local.entity.ActivityEntity
import com.example.havetime.data.model.activity.TimeIntervalDto
import com.example.havetime.data.model.activity.ActivityDto
import com.example.havetime.data.model.activity.LocationDto
import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.model.Location
import com.example.havetime.domain.model.TimeInterval

fun ActivityEntity.toDomain(): Activity {
    return Activity(
        id = id,
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
        }
    )
}

fun Activity.toEntity(): ActivityEntity {
    return ActivityEntity(
        id = id,
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
        }
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
            startTime = timeInterval.startTime,
            endTime = timeInterval.endTime
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
            startTime = timeInterval.startTime,
            endTime = timeInterval.endTime
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