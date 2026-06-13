package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import java.time.LocalDate
import javax.inject.Inject

class GetInitialDateUseCase @Inject constructor(private val repository: DateRepository) {
    operator fun invoke(): LocalDate = repository.getInitialDate()
}