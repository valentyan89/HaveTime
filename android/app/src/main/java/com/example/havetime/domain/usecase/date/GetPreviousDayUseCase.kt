package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import java.time.LocalDate

class GetPreviousDayUseCase(private val repository: DateRepository) {
    operator fun invoke(date: LocalDate): LocalDate = repository.getPreviousDay(date)
}