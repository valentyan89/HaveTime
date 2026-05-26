package com.example.havetime.domain.usecase.activity

import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow

class UpdateActivityUseCase(private val repository: ActivityRepository) {
    operator fun invoke(activity: Activity): Flow<Unit> = repository.updateActivity(activity)
}
