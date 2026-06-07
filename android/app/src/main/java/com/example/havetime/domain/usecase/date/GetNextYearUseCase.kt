package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository

class GetNextYearUseCase(
    private val dateRepository: DateRepository
) {
    suspend operator fun invoke(currentYear: Int): Int {
        return dateRepository.getNextYear(currentYear)
    }
}