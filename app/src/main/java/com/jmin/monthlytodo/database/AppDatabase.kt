package com.jmin.monthlytodo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jmin.monthlytodo.model.Holiday
import com.jmin.monthlytodo.model.Task

@Database(
    entities = [Task::class, Holiday::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun holidayDao(): HolidayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "monthly_todo_database"
                )
                .fallbackToDestructiveMigration() // 添加这行来强制重建数据库
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}