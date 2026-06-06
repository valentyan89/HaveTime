package com.example.havetime.data.repository

import android.util.Log
import com.example.calendar.domain.repository.ActivityRepository
import com.example.havetime.data.local.dao.TodoDao
import com.example.havetime.data.local.dao.UserDao
import com.example.havetime.data.mapper.toDomain
import com.example.havetime.data.mapper.toDto
import com.example.havetime.data.mapper.toEntity
import com.example.havetime.data.mapper.toNetworkDto
import com.example.havetime.data.remote.api.ActivityApi
import com.example.havetime.data.remote.client.KtorClient
import com.example.havetime.data.remote.response.SyncRequest
import com.example.havetime.domain.model.Activity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime

class ActivityRepositoryImpl(
    private val todoDao: TodoDao,
    private val userDao: UserDao,
    private val api: ActivityApi
) : ActivityRepository{
    override fun getTodos(): Flow<List<Activity>> {
        return todoDao.getAllTodos().map { entities ->
            entities
                .map { it.toDomain() }
        }
    }

    override fun getIntervalsForDate(date: LocalDate): Flow<List<Activity>> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.atTime(LocalTime.MAX)
        return todoDao.getTodosByDate(startOfDay, endOfDay).map { entities ->
            entities
                .map { it.toDomain() }
        }
    }

    override fun addTodo(todo: Activity): Flow<Unit> = flow{
        val userServerId = userDao.getUser().firstOrNull()?.serverId ?: 0
        val entity = todo.toEntity().copy(
            userId = userServerId,
            isSynced = false,
            lastTimeModified = System.currentTimeMillis()
        )
        todoDao.insert(entity)
        emit(Unit)
    }

    override fun deleteTodo(id: Int): Flow<Unit> = flow{
        val activity = todoDao.getTodoById(id)
        activity?.let {
            todoDao.update(
                it.copy(
                    isSynced = false,
                    isDeleted = true,
                    lastTimeModified = System.currentTimeMillis()
                )
            )
        }
        emit(Unit)
    }

    override suspend fun syncWithServer(): Result<Unit> {
        return try {
            val user = userDao.getSyncUser()
            Log.d("RRR", "юзер с рума $user")
            user?.let { userRoom ->
                KtorClient.updateToken(userRoom.token)
                val lastSyncTime = todoDao.getLastSyncTimestamp() ?: 0L

                val unsynced = todoDao.getUnsyncedEvents()
                val roomActivities = unsynced.map {it.toDomain().toDto().toNetworkDto()}
                val request = SyncRequest(
                    activities = roomActivities,
                    lastSync = lastSyncTime
                )
                Log.d("RRR", "JSON $request")
                val ids = unsynced.map { it.id }
                val response = api.sync(request)
                val freshDtos = response.map { it.toEntity() }
                todoDao.updateDataAfterSync(freshDtos, ids)
                userDao.insert(
                    user.copy(lastSyncAt = System.currentTimeMillis())
                )
                Result.success(Unit)
            } ?: Result.failure(Exception("User not found"))
        } catch (e: Exception){
            Log.e("RRR", "sync failed ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun updateActivity(activity: Activity): Flow<Unit> = flow {
        val entity = activity.toEntity().copy(
            isSynced = false,
            lastTimeModified = System.currentTimeMillis()
        )
        todoDao.update(entity)
        emit(Unit)
    }
}