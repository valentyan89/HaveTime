package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import kotlinx.coroutines.flow.Flow

class GetTodosUseCase(private val repository: ActivityRepository) {
    operator fun invoke(): Flow<List<Activity>> = repository.getActivities()
}