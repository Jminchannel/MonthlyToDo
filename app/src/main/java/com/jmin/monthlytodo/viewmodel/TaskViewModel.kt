package com.jmin.monthlytodo.viewmodel

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
        loadTasks()
        loadHolidays()
    }
    
    fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = repository.getAllTasks()
        }
    }
    
    fun loadHolidays() {
        viewModelScope.launch {
            _holidays.value = repository.getAllHolidays()
        }
    }
    
    fun getTasksForDate(date: Date): List<Task> {
        return _tasks.value.filter { 
            isSameDay(it.dueDate, date) 
        }.sortedBy { it.order }
    }
    
    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
            loadTasks()
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
            loadTasks()
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            loadTasks()
        }
    }
    
    fun updateTaskOrder(tasks: List<Task>) {
        viewModelScope.launch {
            tasks.forEachIndexed { index, task ->
                repository.updateTask(task.copy(order = index))
            }
            loadTasks()
        }
    }
    
    fun addHoliday(holiday: Holiday) {
        viewModelScope.launch {
            repository.insertHoliday(holiday)
            loadHolidays()
        }
    }
    
    fun deleteHoliday(holiday: Holiday) {
        viewModelScope.launch {
            repository.deleteHoliday(holiday)
            loadHolidays()
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