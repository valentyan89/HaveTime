package com.example.havetime.data.repository

import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.local.dao.ActivityDao
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
    private val activityDao: ActivityDao
) : ActivityRepository{
    override fun getTodos(): Flow<List<Activity>> {
        return activityDao.getAllActivities().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getIntervalsForDate(date: LocalDate): Flow<List<Activity>> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(LocalTime.MAX)
        return activityDao.getActivitiesByDate(startOfDay, endOfDay).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun addTodo(todo: Activity): Flow<Unit> = flow{
        activityDao.insert(todo.toEntity())
        emit(Unit)
    }

    override fun deleteTodo(id: String): Flow<Unit> = flow{
        activityDao.deleteById(id)
        emit(Unit)
    }

    override fun syncWithServer(): Flow<Unit> = flow{
        emit(Unit)
    }

    override fun updateActivity(activity: Activity): Flow<Unit> = flow {
        activityDao.update(activity.toEntity())
        emit(Unit)
    }
}