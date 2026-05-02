package com.example.havetime.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.havetime.data.local.dao.TodoDao
import com.example.havetime.data.local.entity.TodoEntity

@Database(
    entities = [TodoEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TodoDataBase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDataBase? = null

        fun getDatabase(context: Context): TodoDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDataBase::class.java,
                    "todo.db"
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