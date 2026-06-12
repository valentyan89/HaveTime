package com.example.havetime.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.havetime.data.local.dao.TodoDao
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
    abstract fun todoDao(): TodoDao
    abstract fun userDao(): UserDao


}