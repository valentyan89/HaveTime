package com.example.havetime.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.havetime.data.local.dao.ActivityDao
import com.example.havetime.data.local.dao.UserDao
import com.example.havetime.data.local.entity.ActivityEntity
import com.example.havetime.data.local.entity.UserEntity

@Database(
    entities = [
        ActivityEntity::class,
        UserEntity::class
               ],
    version = 2,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class ActivityDataBase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun userDao(): UserDao


}