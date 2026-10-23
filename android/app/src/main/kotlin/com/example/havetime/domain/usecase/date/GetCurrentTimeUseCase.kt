package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

class GetCurrentTimeUseCase(private val repository: DateRepository) {
    operator fun invoke(): Flow<LocalDateTime> = repository.getCurrentTime()
}
