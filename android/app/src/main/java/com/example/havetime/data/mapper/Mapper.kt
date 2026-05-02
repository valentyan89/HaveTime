package com.example.havetime.data.mapper

import com.example.havetime.data.local.entity.TodoEntity
import com.example.havetime.data.model.TimeIntervalDto
import com.example.havetime.data.model.TodoItemDto
import com.example.havetime.domain.model.TodoItem
import com.example.havetime.domain.model.TimeInterval

fun TodoEntity.toDomain(): TodoItem {
    return TodoItem(
        id = id,
        title = title,
        color = color,
        timeInterval = TimeInterval(
            start = timeInterval.start,
            end = timeInterval.end
        )
    )
}

fun TodoItem.toEntity(): TodoEntity {
    return TodoEntity(
        id = id,
        title = title,
        color = color,
        timeInterval = TimeIntervalDto(
            start = timeInterval.start,
            end = timeInterval.end
        )
    )
}

fun TodoItemDto.toEntity(): TodoEntity {
    return TodoEntity(
        id = id,
        title = title,
        color = color,
        timeInterval = timeInterval
    )
}

fun TodoItemDto.toDomain(): TodoItem {
    return TodoItem(
        id = id,
        title = title,
        color = color,
        timeInterval = TimeInterval(
            start = timeInterval.start,
            end = timeInterval.end
        )
    )
}

fun TodoItem.toDto(): TodoItemDto {
    return TodoItemDto(
        id = id,
        title = title,
        color = color,
        timeInterval = TimeIntervalDto(
            start = timeInterval.start,
            end = timeInterval.end
        )
    )
}