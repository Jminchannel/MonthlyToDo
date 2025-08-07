package com.jmin.monthlytodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jmin.monthlytodo.R
import com.jmin.monthlytodo.model.Task
import com.jmin.monthlytodo.ui.theme.Heatmap0
import com.jmin.monthlytodo.ui.theme.Heatmap1
import com.jmin.monthlytodo.ui.theme.Heatmap2
import com.jmin.monthlytodo.ui.theme.Heatmap3
import com.jmin.monthlytodo.ui.theme.Heatmap4
import com.jmin.monthlytodo.ui.theme.Heatmap5
import com.jmin.monthlytodo.ui.theme.Heatmap6
import com.jmin.monthlytodo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.gestures.detectDragGestures

enum class CalendarView {
    MONTH, WEEK
}

@Composable
fun CalendarScreen(viewModel: TaskViewModel) {
    var view by remember { mutableStateOf(CalendarView.MONTH) }
    val currentDate by viewModel.currentDate.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val holidays by viewModel.holidays.collectAsState()
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var totalDragX by remember { mutableFloatStateOf(0f) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit) {
                var zoomFactor = 1f
                
                detectTransformGestures { _, _, zoom, _ ->
                    // 处理缩放手势
                    zoomFactor *= zoom
                    if (zoomFactor > 1.5f) {
                        view = CalendarView.MONTH
                        zoomFactor = 1f
                    } else if (zoomFactor < 0.75f) {
                        view = CalendarView.WEEK
                        zoomFactor = 1f
                    }
                }
            }
            .pointerInput(Unit) {
                // 处理水平滑动手势
                detectDragGestures(
                    onDragEnd = {
                        val threshold = 100f
                        if (totalDragX > threshold) {
                            // 向右滑动 - 上一个月
                            viewModel.navigateToPreviousMonth()
                        } else if (totalDragX < -threshold) {
                            // 向左滑动 - 下一个月
                            viewModel.navigateToNextMonth()
                        }
                        totalDragX = 0f
                    }
                ) { _, dragAmount ->
                    totalDragX += dragAmount.x
                }
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            CalendarHeader(
                currentDate = currentDate,
                onPreviousMonth = { viewModel.navigateToPreviousMonth() },
                onNextMonth = { viewModel.navigateToNextMonth() },
                onToday = { viewModel.navigateToToday() }
            )
        }
        
        item {
            ViewToggle(
                viewType = view,
                onViewTypeChange = { view = it }
            )
        }
        
        item {
            WeekHeaders()
        }

        // 根据视图类型构建日历内容
        when (view) {
            CalendarView.MONTH -> {
                // 月视图：构建日历数据
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                calendar.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))

                val days = mutableListOf<Date>()
                repeat(42) {
                    days.add(calendar.time)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                // 每一行作为独立的item
                for (week in 0 until 6) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            for (day in 0 until 7) {
                                val dayIndex = week * 7 + day
                                val date = days[dayIndex]
                                val tasksForDay = tasks.filter { isSameDay(it.dueDate, date) }
                                val totalTasks = tasksForDay.size
                                val completedTasks = tasksForDay.count { it.isCompleted }
                                val isCurrentMonth = date.month == currentDate.month
                                val isToday = isSameDay(date, Date())
                                val isPast = isPastDate(date)
                                val isHoliday = holidays.any { isSameDay(it.date, date) }
                                
                                Box(modifier = Modifier.weight(1f)) {
                                    CalendarDay(
                                        date = date,
                                        isCurrentMonth = isCurrentMonth,
                                        isToday = isToday,
                                        isPast = isPast,
                                        totalTasks = totalTasks,
                                        completedTasks = completedTasks,
                                        isHoliday = isHoliday,
                                        tasks = tasksForDay,
                                        onClick = { 
                                            selectedDate = date
                                            showTaskDialog = true
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            CalendarView.WEEK -> {
                // 周视图：构建周数据
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                calendar.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - 1))

                val days = mutableListOf<Date>()
                repeat(7) {
                    days.add(calendar.time)
                    calendar.add(Calendar.DAY_OF_MONTH, 1)
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        days.forEach { date ->
                            val tasksForDay = tasks.filter { isSameDay(it.dueDate, date) }
                            val totalTasks = tasksForDay.size
                            val completedTasks = tasksForDay.count { it.isCompleted }
                            val isCurrentMonth = true
                            val isToday = isSameDay(date, Date())
                            val isPast = isPastDate(date)
                            val isHoliday = holidays.any { isSameDay(it.date, date) }
                            
                            Box(modifier = Modifier.weight(1f)) {
                                CalendarDay(
                                    date = date,
                                    isCurrentMonth = isCurrentMonth,
                                    isToday = isToday,
                                    isPast = isPast,
                                    totalTasks = totalTasks,
                                    completedTasks = completedTasks,
                                    isHoliday = isHoliday,
                                    tasks = tasksForDay,
                                    onClick = { 
                                        selectedDate = date
                                        showTaskDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            TaskDensityLegend()
        }
    }

    if (showTaskDialog && selectedDate != null) {
        val tasksForSelectedDate = tasks.filter { isSameDay(it.dueDate, selectedDate!!) }.sortedBy { it.order }
        TaskDialog(
            date = selectedDate!!,
            tasks = tasksForSelectedDate,
            onDismiss = {
                showTaskDialog = false
                selectedDate = null
            },
            onTaskUpdate = { task -> viewModel.updateTask(task) },
            onTaskDelete = { task -> viewModel.deleteTask(task) },
            onTaskReorder = { tasks -> viewModel.updateTaskOrder(tasks) },
            onTaskAdd = { task -> viewModel.addTask(task) }
        )
    }
}

@Composable
fun ViewToggle(
    viewType: CalendarView,
    onViewTypeChange: (CalendarView) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onViewTypeChange(CalendarView.MONTH) },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            colors = if (viewType == CalendarView.MONTH) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant

                )
            },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(stringResource(R.string.month_view))
        }

        Button(
            onClick = { onViewTypeChange(CalendarView.WEEK) },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            colors = if (viewType == CalendarView.WEEK) {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(stringResource(R.string.week_view))
        }
    }
}

@Composable
fun CalendarHeader(
    currentDate: Date,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dateFormat.format(currentDate),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = stringResource(R.string.previous_month)
                )
            }

            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = stringResource(R.string.next_month)
                )
            }
        }
    }
}

@Composable
fun WeekHeaders(
    modifier: Modifier = Modifier
) {
    val daysOfWeek = listOf(
        stringResource(R.string.sun),
        stringResource(R.string.mon),
        stringResource(R.string.tue),
        stringResource(R.string.wed),
        stringResource(R.string.thu),
        stringResource(R.string.fri),
        stringResource(R.string.sat)
    )

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CalendarDay(
    date: Date,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isPast: Boolean,
    totalTasks: Int,
    completedTasks: Int,
    isHoliday: Boolean,
    tasks: List<Task> = emptyList(), // 添加任务列表参数
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val allTasksCompleted = totalTasks > 0 && totalTasks == completedTasks

    val backgroundColor = when {
        allTasksCompleted && !isPast -> MaterialTheme.colorScheme.tertiary
        isPast && !isToday -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        isToday -> MaterialTheme.colorScheme.primary.copy(alpha = if (isDarkTheme) 0.7f else 0.5f)
        !isCurrentMonth -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDarkTheme) 0.3f else 0.7f)
        else -> {
            val heatmapColor = getHeatmapColor(totalTasks)
            if (totalTasks == 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDarkTheme) 0.5f else 0.7f)
            else heatmapColor.copy(alpha = if (isDarkTheme) 0.9f else 1f)
        }
    }

    val baseContentColor = when {
        allTasksCompleted && !isPast -> MaterialTheme.colorScheme.onTertiary
        isPast && !isToday -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        isToday -> MaterialTheme.colorScheme.onPrimary
        !isCurrentMonth -> if (isDarkTheme) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    val contentColor = if (isHoliday && !isPast && !allTasksCompleted) {
        if (isDarkTheme) Color(0xFFFF6B6B) else Color.Red
    } else {
        baseContentColor
    }

    Column(
        modifier = Modifier
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 上部分：日期圆形显示
        Box(
            modifier = Modifier
                .size(48.dp)
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (allTasksCompleted && !isPast) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "All tasks completed",
                    tint = baseContentColor,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                if (totalTasks > 0 && !isPast) {
                    CircularProgressIndicator(
                        progress = completedTasks.toFloat() / totalTasks.toFloat(),
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF4CAF50),
                        strokeWidth = 2.dp,
                        trackColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                    )
                }
                Text(
                    text = SimpleDateFormat("d", Locale.getDefault()).format(date),
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    fontSize = if (isDarkTheme) 14.sp else 12.sp
                )
            }

            if (isHoliday && !isPast && !allTasksCompleted) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isDarkTheme) Color(0xFFFF6B6B) else Color.Red)
                        .align(Alignment.BottomCenter)
                        .offset(y = (-2).dp)
                )
            }
        }

        // 下部分：任务列表（垂直排列）
        if (tasks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Column(
                modifier = Modifier.width(80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                tasks.forEach { task -> // 显示所有任务
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontSize = 8.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (task.isCompleted) {
                                    Color(0xFF4CAF50).copy(alpha = 0.3f) // 淡绿色背景
                                } else {
                                    Color(0xFFFF9800).copy(alpha = 0.3f) // 橙色背景
                                },
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskDensityLegend() {
    Column {
        Text(
            text = stringResource(R.string.task_density),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(listOf(0, 1, 2, 3, 4, 5, 6)) { count ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(getHeatmapColor(count))
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = if (count == 6) "$count+" else count.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

fun getHeatmapColor(taskCount: Int): Color {
    return when (taskCount) {
        0 -> Heatmap0
        1 -> Heatmap1
        2 -> Heatmap2
        3 -> Heatmap3
        4 -> Heatmap4
        5 -> Heatmap5
        else -> Heatmap6
    }
}

fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isPastDate(date: Date): Boolean {
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

    return targetDate.before(today)
}