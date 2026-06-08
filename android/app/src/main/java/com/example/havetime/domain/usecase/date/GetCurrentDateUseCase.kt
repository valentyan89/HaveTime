package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetCurrentDateUseCase(private val repository: DateRepository) {
    operator fun invoke(): Flow<LocalDate> = repository.getCurrentDate()
}