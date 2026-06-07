package com.example.havetime.domain.usecase.activity

import kotlinx.coroutines.flow.Flow
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity

class GetTodosUseCase(private val repository: ActivityRepository) {
    operator fun invoke(): Flow<List<Activity>> = repository.getTodos()
}