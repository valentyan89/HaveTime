package com.example.havetime.domain.usecase.activity

import com.example.havetime.domain.model.Activity
import com.example.havetime.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetIntervalsForDateUseCase(private val repository: ActivityRepository) {
    operator fun invoke(date: LocalDate): Flow<List<Activity>> = repository.getIntervalsForDate(date)
}
