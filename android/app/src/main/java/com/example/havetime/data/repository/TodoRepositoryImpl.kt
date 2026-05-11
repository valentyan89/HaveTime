package com.example.havetime.data.repository

import com.example.calendar.domain.repository.TodoRepository
import com.example.havetime.data.local.dao.TodoDao
import com.example.havetime.data.local.entity.TodoEntity
import com.example.havetime.data.mapper.toDomain
import com.example.havetime.data.mapper.toEntity
import com.example.havetime.data.model.TimeIntervalDto
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TodoRepositoryImpl(
    private val todoDao: TodoDao
) : TodoRepository{
    override fun getTodos(): Flow<List<TodoItem>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIntervalsForDate(date: LocalDate): Flow<List<TodoItem>> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(LocalTime.MAX)
        return todoDao.getTodosByDate(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun addTodo(todo: TodoItem): Flow<Unit> = flow{
        todoDao.insert(todo.toEntity().copy(id = 0))
        emit(Unit)
    }

    override fun deleteTodo(id: Int): Flow<Unit> = flow{
        val entityToDelete = TodoEntity(
            id = id,
            title = "",
            color = 0,
            timeInterval = TimeIntervalDto(
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        )
        todoDao.delete(entityToDelete)
        emit(Unit)
    }

    override fun syncWithServer(): Flow<Unit> = flow{
        emit(Unit)
    }

    override fun saveInterval(interval: TimeInterval): Flow<Unit> = flow{
        TODO("Not yet implemented")
    }
}