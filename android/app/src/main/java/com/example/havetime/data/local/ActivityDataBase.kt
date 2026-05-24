package com.example.havetime.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.havetime.data.local.dao.TodoDao
import com.example.havetime.data.local.dao.ActivityDao
import com.example.havetime.data.local.dao.UserDao
import com.example.havetime.data.local.entity.ActivityEntity
import com.example.havetime.data.local.entity.TodoEntity
import com.example.havetime.data.local.entity.UserEntity

@Database(
    entities = [
        ActivityEntity::class,
        UserEntity::class,
        TodoEntity::class
               ],
    version = 3,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class ActivityDataBase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: ActivityDataBase? = null

        fun getDatabase(context: Context): ActivityDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ActivityDataBase::class.java,
                    "activity.db"
                )
                    .fallbackToDestructiveMigration()
                    // .addMigrations(...)
                    // .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}