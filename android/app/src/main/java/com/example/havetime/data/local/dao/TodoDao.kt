package com.example.havetime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.havetime.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<ActivityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: ActivityEntity)

    @Query("DELETE FROM activity WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM activity")
    fun getAllTodos(): Flow<List<ActivityEntity>>

    @Transaction
    @Query("SELECT * FROM activity WHERE id = :id")
    fun getTodoById(id: Int): ActivityEntity

    @Query("SELECT * FROM activity WHERE startTime >= :dayStart AND startTime <= :dayEnd ORDER BY startTime ASC")
    fun getTodosByDate(dayStart: LocalDateTime, dayEnd: LocalDateTime): Flow<List<ActivityEntity>>
    @Update
    suspend fun update(activity: ActivityEntity)

    @Query("SELECT * FROM activity")
    suspend fun getAllActivitiesSync(): List<ActivityEntity>
}