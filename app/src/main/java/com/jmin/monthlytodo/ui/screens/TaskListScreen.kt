package com.jmin.monthlytodo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.ui.tooling.preview.Preview
import com.jmin.monthlytodo.R
import com.jmin.monthlytodo.model.Priority
import com.jmin.monthlytodo.model.Task
import com.jmin.monthlytodo.ui.theme.PriorityHigh
import com.jmin.monthlytodo.ui.theme.PriorityLow
import com.jmin.monthlytodo.ui.theme.PriorityMedium
import com.jmin.monthlytodo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskListScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    // 筛选状态
    var showFilterDialog by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Date?>(null) }
    var endDate by remember { mutableStateOf<Date?>(null) }
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showCompletedTasks by remember { mutableStateOf(true) }

    // 应用筛选条件
    val filteredTasks = tasks.filter { task ->
        // 日期范围筛选
        val dateInRange = if (startDate != null && endDate != null) {
            task.dueDate.time >= startDate!!.time && task.dueDate.time <= endDate!!.time
        } else true

        // 优先级筛选
        val priorityMatch = selectedPriority?.let { task.priority == it } ?: true

        // 分类筛选
        val categoryMatch = selectedCategory?.let { task.category == it } ?: true

        // 完成状态筛选
        val completionMatch = if (showCompletedTasks) true else !task.isCompleted

        dateInRange && priorityMatch && categoryMatch && completionMatch
    }

    // 按距离当前日期最近排序
    val sortedTasks = filteredTasks.sortedBy { task ->
        val now = Calendar.getInstance().timeInMillis
        val taskTime = task.dueDate.time
        kotlin.math.abs(taskTime - now)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.my_tasks),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { showFilterDialog = true }) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = stringResource(R.string.filter_tasks)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Task list
        if (sortedTasks.isEmpty()) {
            EmptyTaskList()
        } else {
            LazyColumn {
                items(sortedTasks) { task ->
                    TaskItem(
                        task = task,
                        onTaskUpdate = { viewModel.updateTask(it) },
                        onTaskDelete = { viewModel.deleteTask(it) }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // 筛选对话框
        if (showFilterDialog) {
            FilterDialog(
                startDate = startDate,
                endDate = endDate,
                selectedPriority = selectedPriority,
                selectedCategory = selectedCategory,
                showCompletedTasks = showCompletedTasks,
                availableCategories = tasks.map { it.category }.distinct(),
                onStartDateChange = { startDate = it },
                onEndDateChange = { endDate = it },
                onPriorityChange = { selectedPriority = it },
                onCategoryChange = { selectedCategory = it },
                onShowCompletedChange = { showCompletedTasks = it },
                onDismiss = { showFilterDialog = false },
                onClearFilters = {
                    startDate = null
                    endDate = null
                    selectedPriority = null
                    selectedCategory = null
                    showCompletedTasks = true
                }
            )
        }
    }
}

@Composable
fun EmptyTaskList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Task,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = stringResource(R.string.no_tasks_yet),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = stringResource(R.string.add_first_task),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Preview
@Composable
fun letmesee(){
    TaskItem(
        task = Task(id = 1, title = "Task Title", dueDate = Date(), priority = Priority.MEDIUM),
        onTaskUpdate = {},
        onTaskDelete = {}
    )
}
@Composable
fun TaskItem(
    task: Task,
    onTaskUpdate: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit
) {
    var isCompleted by remember { mutableStateOf(task.isCompleted) }
    val dateFormat = SimpleDateFormat("MMM d, hh:mm a", Locale.getDefault())
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = when (task.priority) {
                    Priority.HIGH -> PriorityHigh
                    Priority.MEDIUM -> PriorityMedium
                    Priority.LOW -> PriorityLow
                },
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Task title and drag handle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant 
                           else MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Task description
            if (task.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Task metadata
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category tag
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (task.category.lowercase()) {
                            "work" -> MaterialTheme.colorScheme.primaryContainer
                            "personal" -> MaterialTheme.colorScheme.secondaryContainer
                            "health" -> MaterialTheme.colorScheme.tertiaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Text(
                        text = task.category,
                        style = MaterialTheme.typography.labelMedium,
                        color = when (task.category.lowercase()) {
                            "work" -> MaterialTheme.colorScheme.onPrimaryContainer
                            "personal" -> MaterialTheme.colorScheme.onSecondaryContainer
                            "health" -> MaterialTheme.colorScheme.onTertiaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                
                // Due date and priority
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Priority indicator
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when (task.priority) {
                                    Priority.HIGH -> PriorityHigh
                                    Priority.MEDIUM -> PriorityMedium
                                    Priority.LOW -> PriorityLow
                                }
                            )
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Due date
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = if (isAllDay(task.dueDate)) stringResource(R.string.all_day)
                               else dateFormat.format(task.dueDate),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Action buttons
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { 
                            isCompleted = !isCompleted
                            onTaskUpdate(task.copy(isCompleted = isCompleted))
                        }
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = null // Handled by parent click
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = if (isCompleted) stringResource(R.string.completed) else stringResource(R.string.mark_complete),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Delete button
                IconButton(
                    onClick = { onTaskDelete(task) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_task),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    startDate: Date?,
    endDate: Date?,
    selectedPriority: Priority?,
    selectedCategory: String?,
    showCompletedTasks: Boolean,
    availableCategories: List<String>,
    onStartDateChange: (Date?) -> Unit,
    onEndDateChange: (Date?) -> Unit,
    onPriorityChange: (Priority?) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onShowCompletedChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onClearFilters: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    // 日期选择器状态
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // 标题
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.filter_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    TextButton(onClick = onClearFilters) {
                        Text(stringResource(R.string.clear_all))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 日期范围
                Text(
                    text = stringResource(R.string.date_range),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 开始日期
                    OutlinedTextField(
                        value = startDate?.let { dateFormat.format(it) } ?: "",
                        onValueChange = { },
                        label = { Text(stringResource(R.string.start_date)) },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = {
                                showStartDatePicker = true
                            }) {
                                Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.select_start_date))
                            }
                        }
                    )

                    // 结束日期
                    OutlinedTextField(
                        value = endDate?.let { dateFormat.format(it) } ?: "",
                        onValueChange = { },
                        label = { Text(stringResource(R.string.end_date)) },
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = {
                                showEndDatePicker = true
                            }) {
                                Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.select_end_date))
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 优先级筛选
                Text(
                    text = stringResource(R.string.priority),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Priority.values().forEach { priority ->
                        FilterChip(
                            selected = selectedPriority == priority,
                            onClick = {
                                onPriorityChange(if (selectedPriority == priority) null else priority)
                            },
                            label = { Text(priority.name) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 分类筛选
                Text(
                    text = stringResource(R.string.category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.height(120.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(availableCategories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                onCategoryChange(if (selectedCategory == category) null else category)
                            },
                            label = { Text(category) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 显示已完成任务
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.show_completed_tasks),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Switch(
                        checked = showCompletedTasks,
                        onCheckedChange = onShowCompletedChange
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = onDismiss) {
                        Text(stringResource(R.string.apply))
                    }
                }
            }
        }
    }

    // 开始日期选择器
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = startDate?.time ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onStartDateChange(Date(millis))
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // 结束日期选择器
    if (showEndDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = endDate?.time ?: System.currentTimeMillis()
        )

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onEndDateChange(Date(millis))
                        }
                        showEndDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

fun isAllDay(date: Date): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar.get(Calendar.HOUR_OF_DAY) == 0 &&
           calendar.get(Calendar.MINUTE) == 0 &&
           calendar.get(Calendar.SECOND) == 0
}