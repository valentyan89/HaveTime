package com.example.havetime.presentation.screens.calendar.day_week.timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.havetime.domain.model.TimeInterval
import kotlin.math.roundToLong

internal class TimelineEventDragState(initialInterval: TimeInterval) {
    var interval by mutableStateOf(initialInterval)
        private set

    private var dragStart by mutableStateOf<TimeInterval?>(null)
    private var totalDragPx = 0.0

    val isDragging: Boolean
        get() = dragStart != null

    fun syncInterval(source: TimeInterval) {
        if (!isDragging) interval = source
    }

    fun start() {
        dragStart = interval
        totalDragPx = 0.0
    }

    fun dragBy(deltaPx: Float, minuteHeightPx: Float) {
        val initial = dragStart ?: return
        if (minuteHeightPx <= 0f) return
        totalDragPx += deltaPx
        val deltaMillis = (totalDragPx / minuteHeightPx * 60_000).roundToLong()
        interval = TimeInterval(initial.startTime + deltaMillis, initial.endTime + deltaMillis)
    }

    fun finish(): TimeInterval? {
        if (!isDragging) return null
        dragStart = null
        return interval
    }

    fun cancel() {
        dragStart?.let { interval = it }
        dragStart = null
        totalDragPx = 0.0
    }
}
