package com.jmin.monthlytodo.repository

import com.jmin.monthlytodo.database.TaskDao
import com.jmin.monthlytodo.database.HolidayDao
import com.jmin.monthlytodo.model.Task
import com.jmin.monthlytodo.model.Holiday
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class TaskRepository(private val taskDao: TaskDao, private val holidayDao: HolidayDao) {
    
    suspend fun getAllTasks() = withContext(Dispatchers.IO) {
        taskDao.getAllTasks()
    }
    
    suspend fun getTasksForDate(date: Date) = withContext(Dispatchers.IO) {
        taskDao.getTasksForDate(date)
    }
    
    suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }
    
    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }
    
    suspend fun deleteTask(task: Task) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }
    
    suspend fun getAllHolidays() = withContext(Dispatchers.IO) {
        holidayDao.getAllHolidays()
    }
    
    suspend fun insertHoliday(holiday: Holiday) = withContext(Dispatchers.IO) {
        holidayDao.insertHoliday(holiday)
    }
    
    suspend fun deleteHoliday(holiday: Holiday) = withContext(Dispatchers.IO) {
        holidayDao.deleteHoliday(holiday)
    }
    
    suspend fun getTaskCountForDate(date: Date) = withContext(Dispatchers.IO) {
        taskDao.getTaskCountForDate(date)
    }
}