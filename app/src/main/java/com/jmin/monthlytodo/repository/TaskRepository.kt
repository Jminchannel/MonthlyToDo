package com.jmin.monthlytodo.repository

import android.util.Log
import com.jmin.monthlytodo.database.TaskDao
import com.jmin.monthlytodo.database.HolidayDao
import com.jmin.monthlytodo.model.Task
import com.jmin.monthlytodo.model.Holiday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class TaskRepository(private val taskDao: TaskDao, private val holidayDao: HolidayDao) {
    
    suspend fun getAllTasks() = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Fetching all tasks from database")
            val tasks = taskDao.getAllTasks()
            Log.d("TaskRepository", "Retrieved ${tasks.size} tasks from database")
            tasks
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error fetching all tasks", e)
            throw e
        }
    }
    
    suspend fun getTasksForDate(date: Date) = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Fetching tasks for date: $date")
            val tasks = taskDao.getTasksForDate(date)
            Log.d("TaskRepository", "Retrieved ${tasks.size} tasks for date")
            tasks
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error fetching tasks for date", e)
            throw e
        }
    }
    
    suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Inserting task: ${task.title}")
            val id = taskDao.insertTask(task)
            Log.d("TaskRepository", "Task inserted with ID: $id")
            id
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error inserting task", e)
            throw e
        }
    }
    
    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Updating task: ${task.title}")
            taskDao.updateTask(task)
            Log.d("TaskRepository", "Task updated successfully")
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error updating task", e)
            throw e
        }
    }
    
    suspend fun deleteTask(task: Task) = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Deleting task: ${task.title}")
            taskDao.deleteTask(task)
            Log.d("TaskRepository", "Task deleted successfully")
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error deleting task", e)
            throw e
        }
    }
    
    suspend fun getAllHolidays() = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Fetching all holidays from database")
            val holidays = holidayDao.getAllHolidays()
            Log.d("TaskRepository", "Retrieved ${holidays.size} holidays from database")
            holidays
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error fetching all holidays", e)
            throw e
        }
    }
    
    suspend fun insertHoliday(holiday: Holiday) = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Inserting holiday")
            holidayDao.insertHoliday(holiday)
            Log.d("TaskRepository", "Holiday inserted successfully")
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error inserting holiday", e)
            throw e
        }
    }
    
    suspend fun deleteHoliday(holiday: Holiday) = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Deleting holiday")
            holidayDao.deleteHoliday(holiday)
            Log.d("TaskRepository", "Holiday deleted successfully")
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error deleting holiday", e)
            throw e
        }
    }
    
    suspend fun getTaskCountForDate(date: Date) = withContext(Dispatchers.IO) {
        try {
            Log.d("TaskRepository", "Getting task count for date: $date")
            val count = taskDao.getTaskCountForDate(date)
            Log.d("TaskRepository", "Task count for date: $count")
            count
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error getting task count for date", e)
            throw e
        }
    }
}