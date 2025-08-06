package com.jmin.monthlytodo.database

import androidx.room.*
import com.jmin.monthlytodo.model.Task
import java.util.Date

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY dueDate ASC, `order` ASC")
    suspend fun getAllTasks(): List<Task>
    
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate ORDER BY `order` ASC")
    suspend fun getTasksForDateRange(startDate: Date, endDate: Date): List<Task>
    
    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY `order` ASC")
    suspend fun getTasksForDate(date: Date): List<Task>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("SELECT COUNT(*) FROM tasks WHERE dueDate = :date")
    suspend fun getTaskCountForDate(date: Date): Int
}