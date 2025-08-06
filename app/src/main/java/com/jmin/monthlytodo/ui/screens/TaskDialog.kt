package com.jmin.monthlytodo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import sh.calvin.reorderable.*
import com.jmin.monthlytodo.ui.components.RichTextEditorDialog
import com.jmin.monthlytodo.ui.components.RichTextPreview
import com.jmin.monthlytodo.ui.components.RichTextDisplay
import com.jmin.monthlytodo.R
import com.jmin.monthlytodo.model.Priority
import com.jmin.monthlytodo.model.Task
import com.jmin.monthlytodo.ui.theme.PriorityHigh
import com.jmin.monthlytodo.ui.theme.PriorityLow
import com.jmin.monthlytodo.ui.theme.PriorityMedium
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskDialog(
    date: Date,
    tasks: List<Task>,
    onDismiss: () -> Unit,
    onTaskUpdate: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit,
    onTaskReorder: (List<Task>) -> Unit,
    onTaskAdd: (Task) -> Unit  // 添加新任务的回调
) {
    var taskList by remember { mutableStateOf(tasks) }
    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val haptic = LocalHapticFeedback.current
    val generalCategory = stringResource(R.string.category_general)
    
    // 检查日期是否是今天或未来日期
    val isDateTodayOrFuture = isDateTodayOrFuture(date)

    // 控制添加任务对话框的显示
    var showAddTaskDialog by remember { mutableStateOf(false) }

    // Reorderable状态
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        taskList = taskList.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }

        // 更新order字段并保存到数据库
        val updatedTasks = taskList.mapIndexed { index, task -> task.copy(order = index) }
        taskList = updatedTasks
        onTaskReorder(updatedTasks)

        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateFormat.format(date),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Task count
                if (taskList.isNotEmpty()) {
                    Text(
                        text = "${taskList.size} tasks",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "No tasks for this day",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Task list with drag and drop using Reorderable
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(
                        items = taskList,
                        key = { task ->
                            if (task.id == 0L) {
                                // 对于新创建但尚未分配ID的任务，使用标题作为key
                                "new_${task.title}_${task.hashCode()}"
                            } else {
                                // 对于已有ID的任务，直接使用ID
                                task.id
                            }
                        }
                    ) { task ->
                        ReorderableItem(reorderableLazyListState, key = task.id) { isDragging ->
                            ReorderableTaskItem(
                                task = task,
                                isDragging = isDragging,
                                onTaskUpdate = onTaskUpdate,
                                onTaskDelete = {
                                    // 删除任务
                                    onTaskDelete(it)
                                    taskList = taskList.filter { t -> t.id != it.id }
                                },
                                reorderableScope = this
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Add task button
                Button(
                    onClick = { 
                        showAddTaskDialog = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    // 如果日期是今天或未来的日期，则启用按钮，否则禁用并变灰
                    enabled = isDateTodayOrFuture,
                    colors = if (isDateTodayOrFuture) {
                        ButtonDefaults.buttonColors()
                    } else {
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.add_task))
                }
            }
        }
    }
    
    // 添加任务对话框
    if (showAddTaskDialog) {
        AddTaskDialog(
            date = date,
            onDismiss = { showAddTaskDialog = false },
            onAddTask = { task ->
                onTaskAdd(task)
                // 添加任务后刷新任务列表
                taskList = taskList + listOf(task)
                showAddTaskDialog = false
            }
        )
    }
}


// 添加一个简单的拖拽扩展函数
@Composable
fun ReorderableTaskItem(
    task: Task,
    isDragging: Boolean = false,
    onTaskUpdate: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit,
    reorderableScope: ReorderableCollectionItemScope,
    modifier: Modifier = Modifier
) {
    var isCompleted by remember { mutableStateOf(task.isCompleted) }
    var showEditDialog by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (isDragging) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isDragging) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Drag handle using Reorderable
            IconButton(
                onClick = { },
                modifier = with(reorderableScope) {
                    Modifier.draggableHandle(
                        onDragStarted = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        },
                        onDragStopped = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = "Drag to reorder",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(24.dp),
                    tint = if (isDragging) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            // Checkbox
            Checkbox(
                checked = isCompleted,
                onCheckedChange = { checked ->
                    isCompleted = checked
                    onTaskUpdate(task.copy(isCompleted = checked))
                }
            )
            
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
                    .padding(end = 8.dp)
            )
            
            // Task content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Task title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                           else MaterialTheme.colorScheme.onSurface
                )

                // Task description preview (if has rich content)
                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    RichTextPreview(
                        html = task.description,
                        maxLines = 2
                    )
                }
            }

            // Edit button
            IconButton(
                onClick = { showEditDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit task",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Delete button
            IconButton(
                onClick = { onTaskDelete(task) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // 编辑任务对话框
    if (showEditDialog) {
        EditTaskDialog(
            task = task,
            onDismiss = { showEditDialog = false },
            onUpdateTask = { updatedTask ->
                onTaskUpdate(updatedTask)
                showEditDialog = false
            }
        )
    }
}
@Composable
fun AddTaskDialog(
    date: Date,
    onDismiss: () -> Unit,
    onAddTask: (Task) -> Unit
) {
    val generalCategory = stringResource(R.string.category_general)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(Priority.MEDIUM) }
    var category by remember { mutableStateOf(generalCategory) }
    var selectedTime by remember { mutableStateOf(Calendar.getInstance().apply { time = date }.time) }
    
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.add_task),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Title input
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description input with rich text editor
                var showRichTextEditor by remember { mutableStateOf(false) }
                
                Column {
                    Text(
                        text = stringResource(R.string.task_description),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRichTextEditor = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            if (description.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.click_to_add_description),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                RichTextPreview(
                                    html = description,
                                    maxLines = 3
                                )
                            }
                        }
                    }
                }
                
                if (showRichTextEditor) {
                    RichTextEditorDialog(
                        initialHtml = description,
                        onSave = { html ->
                            description = html
                            showRichTextEditor = false
                        },
                        onDismiss = { showRichTextEditor = false }
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Priority selection
                Text(
                    text = stringResource(R.string.task_priority),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                PriorityDropdown(selectedPriority = priority) { newPriority ->
                    priority = newPriority
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Category selection
                Text(
                    text = stringResource(R.string.task_category),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                CategoryDropdown(
                    selectedCategory = category,
                    onCategorySelected = { category = it }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Date and time selection
                Text(
                    text = stringResource(R.string.task_due_date),
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Date picker
                    Button(
                        onClick = {
                            // Date picker logic would go here
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(dateFormat.format(selectedTime))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Time picker
                    Button(
                        onClick = {
                            // Time picker logic would go here
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(timeFormat.format(selectedTime))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            val newTask = Task(
                                title = title,
                                description = description,
                                priority = priority,
                                category = category,
                                dueDate = selectedTime
                            )
                            onAddTask(newTask)
                            onDismiss()
                        },
                        enabled = title.isNotBlank()
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityDropdown(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val priorities = Priority.values()
    
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selectedPriority.name)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            priorities.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority.name) },
                    onClick = {
                        onPrioritySelected(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf(
        stringResource(R.string.category_general),
        stringResource(R.string.category_work),
        stringResource(R.string.category_personal),
        stringResource(R.string.category_health),
        stringResource(R.string.category_education)
    )
    
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selectedCategory)
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onUpdateTask: (Task) -> Unit
) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }
    var category by remember { mutableStateOf(task.category) }
    var selectedTime by remember { mutableStateOf(task.dueDate) }
    var showRichTextEditor by remember { mutableStateOf(false) }

    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

    if (showRichTextEditor) {
        RichTextEditorDialog(
            initialHtml = description,
            onDismiss = { showRichTextEditor = false },
            onSave = { htmlContent ->
                description = htmlContent
                showRichTextEditor = false
            }
        )
    }
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header
                    Text(
                        text = "Edit Task",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title input
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(R.string.task_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rich text description input
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        onClick = { showRichTextEditor = true }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "description",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TextFormat,
                                        contentDescription = "fullText",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "edit",
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (description.isNotEmpty()) {
                                RichTextPreview(
                                    html = description,
                                    maxLines = 3
                                )
                            } else {
                                Text(
                                    text = "Description",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Priority selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "priority:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        PriorityDropdown(
                            selectedPriority = priority,
                            onPrioritySelected = { priority = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "category:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        CategoryDropdown(
                            selectedCategory = category,
                            onCategorySelected = { category = it }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date and time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateFormat.format(selectedTime),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = timeFormat.format(selectedTime),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.cancel))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val updatedTask = task.copy(
                                        title = title,
                                        description = description,
                                        priority = priority,
                                        category = category,
                                        dueDate = selectedTime
                                    )
                                    onUpdateTask(updatedTask)
                                }
                            },
                            enabled = title.isNotBlank()
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
            }
        }
}



fun isDateTodayOrFuture(date: Date): Boolean {
    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)

    val targetDate = Calendar.getInstance()
    targetDate.time = date
    targetDate.set(Calendar.HOUR_OF_DAY, 0)
    targetDate.set(Calendar.MINUTE, 0)
    targetDate.set(Calendar.SECOND, 0)
    targetDate.set(Calendar.MILLISECOND, 0)

    return targetDate >= today
}