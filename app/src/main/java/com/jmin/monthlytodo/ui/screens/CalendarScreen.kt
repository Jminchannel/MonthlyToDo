package com.jmin.monthlytodo.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.isSystemInDarkTheme // 添加暗色主题检测导入
import androidx.compose.ui.tooling.preview.Preview
import com.jmin.monthlytodo.R
import com.jmin.monthlytodo.model.Priority
import com.jmin.monthlytodo.ui.theme.*
import com.jmin.monthlytodo.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

// 添加视图类型枚举
enum class CalendarViewType {
    MONTH, WEEK
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarScreen(viewModel: TaskViewModel) {
    val currentDate by viewModel.currentDate.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val holidays by viewModel.holidays.collectAsState()
    
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }
    var viewType by remember { mutableStateOf(CalendarViewType.MONTH) }
    var scale by remember { mutableStateOf(1f) }
    var dragOffset by remember { mutableStateOf(0f) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            // 添加手势识别
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { _, _, zoom, _ ->
                        scale *= zoom
                        // 根据缩放比例切换视图
                        if (scale > 1.2f) {
                            viewType = CalendarViewType.WEEK
                            scale = 1f
                        } else if (scale < 0.8f) {
                            viewType = CalendarViewType.MONTH
                            scale = 1f
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        // 根据拖拽距离判断是否切换月份
                        if (dragOffset > 100) {
                            // 向右滑动，切换到上个月
                            viewModel.navigateToPreviousMonth()
                        } else if (dragOffset < -100) {
                            // 向左滑动，切换到下个月
                            viewModel.navigateToNextMonth()
                        }
                        dragOffset = 0f
                    }
                ) { _, dragAmount ->
                    dragOffset += dragAmount.x
                }
            }
    ) {
        // Header with month navigation
        CalendarHeader(
            currentDate = currentDate,
            onPreviousMonth = { viewModel.navigateToPreviousMonth() },
            onNextMonth = { viewModel.navigateToNextMonth() },
            onToday = { viewModel.navigateToToday() }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // View toggle
        ViewToggle(
            viewType = viewType,
            onViewTypeChange = { viewType = it }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Week day headers
        WeekHeaders()
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar grid
        CalendarGrid(
            currentDate = currentDate,
            tasks = tasks,
            holidays = holidays,
            viewType = viewType,
            onDateSelected = { date ->
                selectedDate = date
                showTaskDialog = true
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Task density legend
        TaskDensityLegend()
    }
    
    // Show task dialog when a date is selected
    if (showTaskDialog && selectedDate != null) {
        TaskDialog(
            date = selectedDate!!,
            tasks = viewModel.getTasksForDate(selectedDate!!),
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
fun CalendarHeader(
    currentDate: Date,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    
    Row(
        modifier = Modifier.fillMaxWidth(),
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
//            IconButton(onClick = onToday) {
//                Icon(
//                    imageVector = Icons.Default.Today,
//                    contentDescription = stringResource(R.string.today)
//                )
//            }
            
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
fun ViewToggle(
    viewType: CalendarViewType,
    onViewTypeChange: (CalendarViewType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onViewTypeChange(CalendarViewType.MONTH) },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            colors = if (viewType == CalendarViewType.MONTH) {
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
            onClick = { onViewTypeChange(CalendarViewType.WEEK) },
            modifier = Modifier
                .weight(1f)
                .padding(4.dp),
            colors = if (viewType == CalendarViewType.WEEK) {
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
fun WeekHeaders() {
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
        modifier = Modifier.fillMaxWidth(),
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
@Preview
@Composable
fun iwant2see() {
    CalendarGrid(currentDate = Date(), tasks = emptyList(), holidays = emptyList(), viewType = CalendarViewType.MONTH) { }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun CalendarGrid(
    currentDate: Date,
    tasks: List<com.jmin.monthlytodo.model.Task>,
    holidays: List<com.jmin.monthlytodo.model.Holiday>,
    viewType: CalendarViewType,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = currentDate
    
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    
    // 根据视图类型调整日期范围
    val days = if (viewType == CalendarViewType.MONTH) {
        // Set calendar to first day of month
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        
        // Adjust to start from Sunday
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))
        
        val monthDays = mutableListOf<Date>()
        repeat(42) { // 6 weeks * 7 days
            monthDays.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        monthDays
    } else {
        // Week view - show current week
        val weekDays = mutableListOf<Date>()
        // Adjust to start from Sunday of current week
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        calendar.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - 1))
        
        repeat(7) {
            weekDays.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        weekDays
    }
    
    val columns = if (viewType == CalendarViewType.MONTH) 7 else 7
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp), // 增加水平间距
        verticalArrangement = Arrangement.spacedBy(4.dp)    // 增加垂直间距
    ) {
        items(days) { date ->
            val dateCalendar = Calendar.getInstance()
            dateCalendar.time = date
            
            val isCurrentMonth = dateCalendar.get(Calendar.MONTH) == currentMonth &&
                    dateCalendar.get(Calendar.YEAR) == currentYear
            
            val isToday = isSameDay(date, Calendar.getInstance().time)
            
            val taskCount = tasks.count { isSameDay(it.dueDate, date) }
            val isHoliday = holidays.any { isSameDay(it.date, date) }
            
            CalendarDay(
                date = date,
                isCurrentMonth = isCurrentMonth,
                isToday = isToday,
                taskCount = taskCount,
                isHoliday = isHoliday,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
fun CalendarDay(
    date: Date,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    taskCount: Int,
    isHoliday: Boolean,
    onClick: () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    
    // 优化夜间模式下的背景色
    val backgroundColor = when {
        isToday -> {
            // 夜间模式下使用更鲜明的今日背景色
            if (isDarkTheme) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            }
        }
        !isCurrentMonth -> {
            // 非当前月日期在夜间模式下使用更深的背景色
            if (isDarkTheme) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            }
        }
        else -> {
            // 普通日期在夜间模式下使用更鲜明的热力图颜色
            val heatmapColor = getHeatmapColor(taskCount)
            if (isDarkTheme) {
                when (taskCount) {
                    0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else -> heatmapColor.copy(alpha = 0.9f)
                }
            } else {
                heatmapColor.copy(alpha = if (taskCount == 0) 0.7f else 1f)
            }
        }
    }
    
    // 优化夜间模式下的文字颜色
    val textColor = when {
        isToday -> {
            // 今日使用对比度更高的文字颜色
            if (isDarkTheme) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        }
        !isCurrentMonth -> {
            // 非当前月日期使用更清晰的灰色
            if (isDarkTheme) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        }
        else -> {
            // 普通日期使用更清晰的文字颜色
            if (isDarkTheme) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        }
    }
    
    // 节日以红色标记
    val dayTextColor = if (isHoliday) {
        // 在暗黑模式下使用更明亮的红色以提高可见性
        if (isDarkTheme) {
            Color(0xFFFF6B6B) // 明亮的红色，适合暗黑模式
        } else {
            Color.Red
        }
    } else {
        textColor
    }
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp) // 添加内边距，使日期元素更小，增加间距效果
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = SimpleDateFormat("d", Locale.getDefault()).format(date),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isToday) dayTextColor else textColor,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
            fontSize = if (isDarkTheme) 16.sp else 14.sp // 夜间模式下增大字体
        )
        
        // 如果是节日，添加底部标记点
        if (isHoliday) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) Color(0xFFFF6B6B) else Color.Red)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-4).dp)
            )
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
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance()
    cal1.time = date1
    cal2.time = date2
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}