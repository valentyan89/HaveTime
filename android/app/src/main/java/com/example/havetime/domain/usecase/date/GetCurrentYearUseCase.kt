package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentYearUseCase(private val repository: DateRepository) {
    operator fun invoke(): Flow<Int> = repository.getCurrentYear()
}