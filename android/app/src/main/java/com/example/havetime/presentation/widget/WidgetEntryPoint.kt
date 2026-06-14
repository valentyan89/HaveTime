package com.example.havetime.presentation.widget

import com.example.havetime.domain.usecase.activity.GetUpcomingActivitiesUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun getUpcomingActivitiesUseCase(): GetUpcomingActivitiesUseCase
}