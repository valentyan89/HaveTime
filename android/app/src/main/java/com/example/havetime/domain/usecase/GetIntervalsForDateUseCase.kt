package com.example.havetime.domain.usecase

import com.example.havetime.domain.model.TimeInterval
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GetIntervalsForDateUseCase {
    fun execute(allIntervals: List<TimeInterval>, targetDate: LocalDate): List<TimeInterval> {
        return allIntervals.mapNotNull { interval ->
            val start = interval.start
            val end = interval.end
            val startDate = start.toLocalDate()
            val endDate = end.toLocalDate()

            when {
                startDate == targetDate && endDate == targetDate -> interval

                startDate == targetDate && endDate.isAfter(targetDate) -> {
                    interval.copy(end = LocalDateTime.of(targetDate, LocalTime.MAX))
                }
                endDate == targetDate && startDate.isBefore(targetDate) -> {
                    interval.copy(start = LocalDateTime.of(targetDate, LocalTime.MIN))
                }
                startDate.isBefore(targetDate) && endDate.isAfter(targetDate) -> {
                    interval.copy(
                        start = LocalDateTime.of(targetDate, LocalTime.MIN),
                        end = LocalDateTime.of(targetDate, LocalTime.MAX)
                    )
                }

                else -> null
            }
        }
    }
}