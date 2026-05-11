package com.example.havetime.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.havetime.data.local.entity.TodoEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<TodoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)

    @Query("SELECT * FROM todo")
    fun getAllTodos(): Flow<List<TodoEntity>>

    @Transaction
    @Query("SELECT * FROM todo WHERE id = :id")
    fun getTodoById(id: Int): TodoEntity

    @Query("SELECT * FROM todo WHERE start >= :dayStart AND start <= :dayEnd ORDER BY start ASC")
    fun getTodosByDate(dayStart: LocalDateTime, dayEnd: LocalDateTime): Flow<List<TodoEntity>>
}