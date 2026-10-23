package com.example.havetime.presentation.screens.calendar.day_week.timeline

import com.example.havetime.domain.model.TimeInterval
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class TimelineEventDragStateTest {
    private val original = TimeInterval(3_600_000L, 7_200_000L)

    @Test
    fun smallMovementsAccumulateBeforeConvertingToTime() {
        val state = TimelineEventDragState(original)
        state.start()

        repeat(10) { state.dragBy(0.25f, minuteHeightPx = 1.5f) }

        assertEquals(TimeInterval(3_700_000L, 7_300_000L), state.finish())
    }

    @Test
    fun secondDragUsesTheIntervalReturnedFromDatabase() {
        val state = TimelineEventDragState(original)
        // The gesture handler retains this same reference across recompositions.
        val gestureState = state
        state.start()
        state.dragBy(15f, minuteHeightPx = 1.5f)
        val saved = state.finish()!!
        state.syncInterval(saved)

        gestureState.start()
        gestureState.dragBy(15f, minuteHeightPx = 1.5f)

        assertEquals(TimeInterval(4_800_000L, 8_400_000L), gestureState.finish())
    }

    @Test
    fun externalTimeUpdateIsVisibleToTheExistingGestureHandler() {
        val state = TimelineEventDragState(original)
        val gestureState = state
        state.syncInterval(TimeInterval(7_200_000L, 10_800_000L))

        gestureState.start()
        gestureState.dragBy(-15f, minuteHeightPx = 1.5f)

        assertEquals(TimeInterval(6_600_000L, 10_200_000L), gestureState.finish())
    }

    @Test
    fun databaseEmissionDoesNotResetAnActiveDrag() {
        val state = TimelineEventDragState(original)
        state.start()
        state.dragBy(15f, minuteHeightPx = 1.5f)

        state.syncInterval(original)

        assertEquals(TimeInterval(4_200_000L, 7_800_000L), state.finish())
    }

    @Test
    fun cancellationRestoresTheIntervalAndClearsAccumulatedMovement() {
        val state = TimelineEventDragState(original)
        state.start()
        state.dragBy(15f, minuteHeightPx = 1.5f)
        state.cancel()

        assertEquals(original, state.interval)
        assertFalse(state.isDragging)
        assertNull(state.finish())

        state.start()
        state.dragBy(-1.5f, minuteHeightPx = 1.5f)
        assertEquals(TimeInterval(3_540_000L, 7_140_000L), state.finish())
    }
}
