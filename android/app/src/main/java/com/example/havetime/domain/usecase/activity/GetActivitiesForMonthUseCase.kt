package com.example.havetime.domain.usecase.activity


import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth

class GetActivitiesForMonthUseCase(
    private val repository: ActivityRepository
) {
    operator fun invoke(yearMonth: YearMonth): Flow<List<Activity>> {
        return repository.getActivitiesForMonth(yearMonth)
    }
}