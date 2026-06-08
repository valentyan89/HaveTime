package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import java.time.LocalDate

class GetNextWeekUseCase(private val repository: DateRepository) {
    operator fun invoke(date: LocalDate): LocalDate = repository.getNextWeek(date)
}