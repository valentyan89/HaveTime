package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import java.time.LocalDateTime
import javax.inject.Inject

class GetInitialDateTimeUseCase @Inject constructor(private val repository: DateRepository) {
    operator fun invoke(): LocalDateTime = repository.getInitialDateTime()
}