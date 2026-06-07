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

    @Query("DELETE FROM activity")
    suspend fun deleteAllActivities()

    @Query("SELECT * FROM activity WHERE isDeleted = 0")
    fun getAllTodos(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activity WHERE id = :id AND isDeleted = 0")
    suspend fun getTodoById(id: Int): ActivityEntity?

    @Query("SELECT * FROM activity WHERE startTime >= :dayStart AND startTime <= :dayEnd AND isDeleted = 0 ORDER BY startTime ASC")
    fun getTodosByDate(dayStart: Long, dayEnd: Long): Flow<List<ActivityEntity>>

    @Update
    suspend fun update(activity: ActivityEntity)

    @Query("SELECT * FROM activity WHERE isSynced = 0 ORDER BY lastTimeModified ASC")
    suspend fun getUnsyncedEvents(): List<ActivityEntity>

    @Query("UPDATE activity SET isSynced = 1 WHERE id IN (:syncedIds)")
    suspend fun markEventsSynced(syncedIds: List<Int>)

    @Query("SELECT MAX(lastTimeModified) FROM activity WHERE isSynced = 1")
    suspend fun getLastSyncTimestamp(): Long?

    @Query("DELETE FROM activity WHERE isDeleted = 1 AND isSynced = 1")
    suspend fun clearDeletedSynced()

    @Transaction
    suspend fun updateDataAfterSync(freshActivities: List<ActivityEntity>, syncedIds: List<Int>) {
        clearDeletedSynced()
        markEventsSynced(syncedIds)
        insertAll(freshActivities)
    }

    @Query("SELECT * FROM activity WHERE title LIKE :searchQuery AND isDeleted = 0")
    fun searchActivities(searchQuery: String): Flow<List<ActivityEntity>>

    @Query("UPDATE activity SET userId = :userId WHERE userId = 0 AND isDeleted = 0")
    suspend fun setTasksBeforeLogin(userId: Int)
}