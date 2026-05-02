package com.example.havetime.domain.usecase

import kotlinx.coroutines.flow.Flow
import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.domain.model.TodoItem
import java.time.LocalDate

class GetIntervalsForDateUseCase(private val repository: TodoRepository) {
    operator fun invoke(date: LocalDate): Flow<List<TodoItem>> = repository.getIntervalsForDate(date)
}