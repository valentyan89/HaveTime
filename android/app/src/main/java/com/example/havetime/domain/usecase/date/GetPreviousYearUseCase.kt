package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository

class GetPreviousYearUseCase(
    private val dateRepository: DateRepository
) {
     operator fun invoke(currentYear: Int): Int {
        return dateRepository.getPreviousYear(currentYear)
    }
}