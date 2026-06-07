package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import kotlinx.coroutines.flow.Flow

class SearchUseCase(private val activityRepository: ActivityRepository) {
    operator fun invoke(query: String): Flow<List<Activity>> = activityRepository.searchActivities(query)
}
