package com.example.havetime.data.repository

<<<<<<<< HEAD:android/app/src/main/java/com/example/havetime/data/repository/TodoRepositoryImpl.kt
import com.example.calendar.domain.repository.TodoRepository

// Файл оставлен для обратной совместимости
// Рекомендуется использовать ActivityRepositoryImpl.kt
========
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.local.dao.TodoDao
import com.example.havetime.data.mapper.toDomain
import com.example.havetime.data.mapper.toEntity
import com.example.havetime.domain.model.TimeInterval
import com.example.havetime.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime

class ActivityRepositoryImpl(
    private val todoDao: TodoDao
) : ActivityRepository{
    override fun getTodos(): Flow<List<Activity>> {
        return todoDao.getAllTodos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIntervalsForDate(date: LocalDate): Flow<List<Activity>> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(LocalTime.MAX)
        return todoDao.getTodosByDate(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun addTodo(todo: Activity): Flow<Unit> = flow{
        todoDao.insert(todo.toEntity().copy(id = 0))
        emit(Unit)
    }

    override fun deleteTodo(id: Int): Flow<Unit> = flow{
        todoDao.delete(id)
        emit(Unit)
    }

    override fun syncWithServer(): Flow<Unit> = flow{
        emit(Unit)
    }

    override fun updateActivity(activity: Activity): Flow<Unit> = flow {
        todoDao.update(activity.toEntity())
        emit(Unit)
    }
}
>>>>>>>> origin/clean_valya:android/app/src/main/java/com/example/havetime/data/repository/ActivityRepositoryImpl.kt
