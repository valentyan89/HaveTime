package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

class GetCurrentDateUseCase(private val repository: DateRepository) {
    operator fun invoke(): Flow<LocalDate> = repository.getCurrentDate()
}
