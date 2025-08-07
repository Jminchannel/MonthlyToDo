package com.jmin.monthlytodo.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmin.monthlytodo.model.Task
import com.jmin.monthlytodo.model.Holiday
import com.jmin.monthlytodo.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks
    
    private val _holidays = MutableStateFlow<List<Holiday>>(emptyList())
    val holidays: StateFlow<List<Holiday>> = _holidays
    
    private val _currentDate = MutableStateFlow(Calendar.getInstance().time)
    val currentDate: StateFlow<Date> = _currentDate
    
    init {
        Log.d("TaskViewModel", "Initializing TaskViewModel")
        loadTasks()
        loadHolidays()
    }
    
    fun loadTasks() {
        viewModelScope.launch {
            try {
                Log.d("TaskViewModel", "Loading tasks...")
                val taskList = repository.getAllTasks()
                Log.d("TaskViewModel", "Loaded ${taskList.size} tasks")
                _tasks.value = taskList
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error loading tasks", e)
            }
        }
    }
    
    fun loadHolidays() {
        viewModelScope.launch {
            try {
                Log.d("TaskViewModel", "Loading holidays...")
                val holidayList = repository.getAllHolidays()
                Log.d("TaskViewModel", "Loaded ${holidayList.size} holidays")
                _holidays.value = holidayList
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error loading holidays", e)
            }
        }
    }
    
    fun getTasksForDate(date: Date): List<Task> {
        return _tasks.value.filter { 
            isSameDay(it.dueDate, date) 
        }.sortedBy { it.order }
    }
    
    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                Log.d("TaskViewModel", "Adding task: ${task.title}")
                val newTaskId = repository.insertTask(task)
                Log.d("TaskViewModel", "Task added with ID: $newTaskId")
                
                // 立即在本地添加任务
                val newTask = task.copy(id = newTaskId)
                val currentTasks = _tasks.value.toMutableList()
                currentTasks.add(newTask)
                _tasks.value = currentTasks.toList()
                
                Log.d("TaskViewModel", "Local task list updated, now has ${_tasks.value.size} tasks")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error adding task", e)
            }
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                Log.d("TaskViewModel", "Updating task: ${task.title}")
                // 首先在本地更新列表，以即时反映UI变化
                val currentTasks = _tasks.value.toMutableList()
                val index = currentTasks.indexOfFirst { it.id == task.id }
                if (index != -1) {
                    currentTasks[index] = task
                    _tasks.value = currentTasks.toList()
                    Log.d("TaskViewModel", "Local task updated")
                }
                // 然后在后台更新数据库
                repository.updateTask(task)
                Log.d("TaskViewModel", "Database task updated")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task", e)
                // 如果数据库更新失败，重新加载以保持一致性
                loadTasks()
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                Log.d("TaskViewModel", "Deleting task: ${task.title}")
                // 首先在本地删除
                val currentTasks = _tasks.value.toMutableList()
                currentTasks.removeAll { it.id == task.id }
                _tasks.value = currentTasks.toList()
                Log.d("TaskViewModel", "Local task deleted")
                
                // 然后从数据库删除
                repository.deleteTask(task)
                Log.d("TaskViewModel", "Database task deleted")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error deleting task", e)
                // 如果数据库删除失败，重新加载以保持一致性
                loadTasks()
            }
        }
    }
    
    fun updateTaskOrder(tasks: List<Task>) {
        viewModelScope.launch {
            try {
                Log.d("TaskViewModel", "Updating task order for ${tasks.size} tasks")
                tasks.forEachIndexed { index, task ->
                    repository.updateTask(task.copy(order = index))
                }
                // 立即更新本地状态
                _tasks.value = tasks
                Log.d("TaskViewModel", "Task order updated")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task order", e)
                loadTasks()
            }
        }
    }
    
    fun addHoliday(holiday: Holiday) {
        viewModelScope.launch {
            try {
                Log.d("TaskViewModel", "Adding holiday")
                repository.insertHoliday(holiday)
                loadHolidays()
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error adding holiday", e)
            }
        }
    }
    
    fun deleteHoliday(holiday: Holiday) {
        viewModelScope.launch {
            try {
                Log.d("TaskViewModel", "Deleting holiday")
                repository.deleteHoliday(holiday)
                loadHolidays()
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error deleting holiday", e)
            }
        }
    }
    
    fun isHoliday(date: Date): Boolean {
        return _holidays.value.any { isSameDay(it.date, date) }
    }
    
    fun getTaskCountForDate(date: Date): Int {
        return _tasks.value.count { isSameDay(it.dueDate, date) }
    }
    
    fun navigateToPreviousMonth() {
        val calendar = Calendar.getInstance()
        calendar.time = _currentDate.value
        calendar.add(Calendar.MONTH, -1)
        _currentDate.value = calendar.time
    }
    
    fun navigateToNextMonth() {
        val calendar = Calendar.getInstance()
        calendar.time = _currentDate.value
        calendar.add(Calendar.MONTH, 1)
        _currentDate.value = calendar.time
    }
    
    fun navigateToToday() {
        _currentDate.value = Calendar.getInstance().time
    }
    
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}