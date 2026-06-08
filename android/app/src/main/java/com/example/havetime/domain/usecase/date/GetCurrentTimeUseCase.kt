package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

class GetCurrentTimeUseCase(private val repository: DateRepository) {
    operator fun invoke(): Flow<LocalDateTime> = repository.getCurrentTime()
}