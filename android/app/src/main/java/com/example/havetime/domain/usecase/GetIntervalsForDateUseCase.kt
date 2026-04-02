package com.example.havetime.domain.usecase

import android.icu.util.Calendar
import com.example.havetime.domain.model.TimeInterval

class GetIntervalsForDateUseCase {
    fun execute(allIntervals: List<TimeInterval>, targetDate: Calendar): List<TimeInterval> {
        val targetDay = targetDate.get(Calendar.DAY_OF_YEAR)
        val targetYear = targetDate.get(Calendar.YEAR)

        return allIntervals.mapNotNull { interval ->
            val startDay = interval.start.get(Calendar.DAY_OF_YEAR)
            val startYear = interval.start.get(Calendar.YEAR)
            val endDay = interval.end.get(Calendar.DAY_OF_YEAR)
            val endYear = interval.end.get(Calendar.YEAR)

            val isSameDayStart = startDay == targetDay && startYear == targetYear
            val isSameDayEnd = endDay == targetDay && endYear == targetYear

            when {
                isSameDayStart && isSameDayEnd -> interval
                isSameDayStart && (endDay > targetDay || endYear > startYear) -> {
                    val endOfDay = (targetDate.clone() as Calendar).apply {
                        set(Calendar.HOUR_OF_DAY, 23); set(Calendar.MINUTE, 59)
                    }
                    interval.copy(end = endOfDay)
                }
                isSameDayEnd && (startDay < targetDay || startYear < endYear) -> {
                    val startOfDay = (targetDate.clone() as Calendar).apply {
                        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    }
                    interval.copy(start = startOfDay)
                }
                else -> null
            }
        }
    }
}