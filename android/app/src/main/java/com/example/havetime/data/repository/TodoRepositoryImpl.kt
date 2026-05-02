package com.example.havetime.data.repository

import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.data.local.dao.TodoDao
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class TodoRepositoryImpl(
    private val todoDao: TodoDao
) : TodoRepository{
    override fun getTodos(): Flow<List<TodoItem>> {
        TODO("Not yet implemented")
    }

    override fun getIntervalsForDate(date: LocalDate): Flow<List<TimeInterval>> {
        TODO("Not yet implemented")
    }

    override fun addTodo(todo: TodoItem): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override fun deleteTodo(id: Int): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override fun syncWithServer(): Flow<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun saveInterval(interval: TimeInterval) {
        TODO("Not yet implemented")
    }
}