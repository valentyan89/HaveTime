package com.example.havetime.domain.usecase.date

import com.example.havetime.domain.repository.DateRepository
import java.time.YearMonth

class GetPreviousMonthUseCase(private val repository: DateRepository) {
    operator fun invoke(yearMonth: YearMonth): YearMonth = repository.getPreviousMonth(yearMonth)
}