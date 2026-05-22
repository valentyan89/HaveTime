package com.example.havetime.domain.usecase.activity

import kotlinx.coroutines.flow.Flow
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import java.time.LocalDate

class GetIntervalsForDateUseCase(private val repository: ActivityRepository) {
    operator fun invoke(date: LocalDate): Flow<List<Activity>> = repository.getIntervalsForDate(date)
}