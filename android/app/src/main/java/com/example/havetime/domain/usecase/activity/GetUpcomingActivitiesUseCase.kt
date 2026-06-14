package com.example.havetime.domain.usecase.activity

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.domain.model.Activity
import javax.inject.Inject

class GetUpcomingActivitiesUseCase @Inject constructor(private val repository: ActivityRepository) {
    suspend operator fun invoke(limit: Int = 3): List<Activity> = repository.getUpcomingActivities(limit)
}