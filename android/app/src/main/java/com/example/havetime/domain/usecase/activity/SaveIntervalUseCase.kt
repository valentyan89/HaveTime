package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.TimeInterval
import kotlinx.coroutines.flow.Flow

class SaveIntervalUseCase(private val repository: ActivityRepository) {
    operator fun invoke(interval: TimeInterval): Flow<Unit> = repository.saveInterval(interval)
}