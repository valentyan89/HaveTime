package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

class GetActivitiesForMonthUseCase(private val repository: ActivityRepository) {
    operator fun invoke(yearMonth: YearMonth): Flow<List<Activity>> =
        repository.getActivitiesForMonth(yearMonth)
}
