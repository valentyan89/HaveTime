//package com.example.havetime.data.local.dao
//
//import androidx.room.Dao
//import androidx.room.Delete
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import androidx.room.Update
//import com.example.havetime.data.local.entity.ActivityEntity
//import kotlinx.coroutines.flow.Flow
//import java.time.LocalDateTime
//
//@Dao
//interface ActivityDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(activity: ActivityEntity)
//
//    @Delete
//    suspend fun delete(activity: ActivityEntity)
//
//    @Query("DELETE FROM activity WHERE id = :id")
//    suspend fun deleteById(id: String)
//
//    @Update
//    suspend fun update(activity: ActivityEntity)
//
//    @Query("SELECT * FROM activity")
//    fun getAllActivities(): Flow<List<ActivityEntity>>
//
//    @Query("SELECT * FROM activity WHERE start >= :dayStart AND start <= :dayEnd ORDER BY start ASC")
//    fun getActivitiesByDate(dayStart: LocalDateTime, dayEnd: LocalDateTime): Flow<List<ActivityEntity>>
//}