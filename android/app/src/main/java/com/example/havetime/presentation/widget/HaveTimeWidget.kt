package com.example.havetime.presentation.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.delay

class HaveTimeWidget : GlanceAppWidget() {
    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context = context.applicationContext,
            entryPoint = WidgetEntryPoint::class.java
        )

        val useCase = entryPoint.getUpcomingActivitiesUseCase()

        delay(1000)

        val upComingActivities = try {
            useCase()
        } catch (e: Exception){
            emptyList()
        }



        provideContent {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                if (upComingActivities.isEmpty()) {
                    Text(text = "Нет ближайших задач (или пустая БД)")
                } else {
                    upComingActivities.forEach { activity ->
                        Text(activity.title)
                    }
                }
            }
        }
    }
}