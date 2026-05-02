package com.example.calendar.domain.repository

import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.TodoItem
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun getTodos(): Flow<List<TodoItem>>
    fun getIntervalsForDate(date: LocalDate): Flow<List<TodoItem>>
    fun addTodo(todo: TodoItem): Flow<Unit>
    fun deleteTodo(id: Int): Flow<Unit>
    fun syncWithServer(): Flow<Unit>
    fun saveInterval(interval: TimeInterval): Flow<Unit>
}