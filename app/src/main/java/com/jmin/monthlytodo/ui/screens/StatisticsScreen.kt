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
import com.jmin.monthlytodo.R
import com.jmin.monthlytodo.model.Task
import com.jmin.monthlytodo.viewmodel.TaskViewModel
import java.util.*

@Composable
fun StatisticsScreen(viewModel: TaskViewModel) {
    var viewType by remember { mutableStateOf(StatisticsViewType.YEARLY) }
    val tasks by viewModel.tasks.collectAsState()
    var selectedPeriod by remember { mutableStateOf<String?>(null) }

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
                text = stringResource(R.string.statistics),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            // View toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                FilterChip(
                    selected = (viewType == StatisticsViewType.MONTHLY),
                    onClick = { viewType = StatisticsViewType.MONTHLY },
                    label = { Text(stringResource(R.string.monthly_view)) }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FilterChip(
                    selected = (viewType == StatisticsViewType.YEARLY),
                    onClick = { viewType = StatisticsViewType.YEARLY },
                    label = { Text(stringResource(R.string.yearly_view)) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Completion rate chart
        CompletionRateChart(
            tasks = tasks,
            viewType = viewType,
            onPeriodSelected = { period ->
                selectedPeriod = period
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Task categories
        TaskCategories(
            tasks = tasks,
            viewType = viewType,
            selectedPeriod = selectedPeriod
        )
    }
}

@Composable
fun CompletionRateChart(
    tasks: List<Task>,
    viewType: StatisticsViewType,
    onPeriodSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.completion_rate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Chart visualization
            val chartData = getCompletionRateData(tasks, viewType)
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                chartData.forEach { (label, percentage) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPeriodSelected(label) }
                    ) {
                        // Bar chart
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .width(24.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(24.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(percentage / 100f)
                                    .width(24.dp)
                                    .align(Alignment.BottomCenter)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        // Percentage
                        Text(
                            text = "${percentage.toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Label
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCategories(
    tasks: List<Task>,
    viewType: StatisticsViewType,
    selectedPeriod: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.task_categories),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val categoryData = getTaskCategoryData(tasks, viewType, selectedPeriod)
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categoryData.toList()) { (category, percentage) ->
                    CategoryProgressItem(
                        category = category,
                        percentage = percentage
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryProgressItem(
    category: String,
    percentage: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = "${percentage.toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when (category.lowercase()) {
                "work" -> MaterialTheme.colorScheme.primary
                "personal" -> MaterialTheme.colorScheme.secondary
                "health" -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.primary
            }
        )
    }
}

@Composable
fun getCompletionRateData(tasks: List<Task>, viewType: StatisticsViewType): List<Pair<String, Float>> {
    return if (viewType == StatisticsViewType.MONTHLY) {
        // Get last 6 months
        val data = mutableListOf<Pair<String, Float>>()
        val currentCalendar = Calendar.getInstance()

        for (i in 5 downTo 0) {
            val monthCalendar = Calendar.getInstance()
            monthCalendar.add(Calendar.MONTH, -i)

            val month = monthCalendar.get(Calendar.MONTH)
            val year = monthCalendar.get(Calendar.YEAR)

            val monthTasks = tasks.filter {
                val taskCalendar = Calendar.getInstance()
                taskCalendar.time = it.dueDate
                taskCalendar.get(Calendar.MONTH) == month && taskCalendar.get(Calendar.YEAR) == year
            }

            val completedTasks = monthTasks.count { it.isCompleted }
            val completionRate = if (monthTasks.isEmpty()) 0f else (completedTasks.toFloat() / monthTasks.size) * 100f

            val monthName = getMonthAbbreviation(month)

            data.add(Pair(monthName, completionRate))
        }
        data
    } else {
        // Yearly view - show last 5 years
        val data = mutableListOf<Pair<String, Float>>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        for (i in 4 downTo 0) {
            val year = currentYear - i

            val yearTasks = tasks.filter {
                val taskCalendar = Calendar.getInstance()
                taskCalendar.time = it.dueDate
                taskCalendar.get(Calendar.YEAR) == year
            }

            val completedTasks = yearTasks.count { it.isCompleted }
            val completionRate = if (yearTasks.isEmpty()) 0f else (completedTasks.toFloat() / yearTasks.size) * 100f

            data.add(Pair(year.toString(), completionRate))
        }
        data
    }
}

@Composable
fun getMonthAbbreviation(month: Int): String {
    return when (month) {
        0 -> stringResource(R.string.jan)
        1 -> stringResource(R.string.feb)
        2 -> stringResource(R.string.mar)
        3 -> stringResource(R.string.apr)
        4 -> stringResource(R.string.may_short)
        5 -> stringResource(R.string.jun)
        6 -> stringResource(R.string.jul)
        7 -> stringResource(R.string.aug)
        8 -> stringResource(R.string.sep)
        9 -> stringResource(R.string.oct)
        10 -> stringResource(R.string.nov)
        11 -> stringResource(R.string.dec)
        else -> ""
    }
}

@Composable
fun getMonthAbbreviations(): Array<String> {
    return arrayOf(
        stringResource(R.string.jan),
        stringResource(R.string.feb),
        stringResource(R.string.mar),
        stringResource(R.string.apr),
        stringResource(R.string.may_short),
        stringResource(R.string.jun),
        stringResource(R.string.jul),
        stringResource(R.string.aug),
        stringResource(R.string.sep),
        stringResource(R.string.oct),
        stringResource(R.string.nov),
        stringResource(R.string.dec)
    )
}

@Composable
fun getTaskCategoryData(
    tasks: List<Task>,
    viewType: StatisticsViewType,
    selectedPeriod: String?
): Map<String, Float> {
    // 如果没有选择特定时期，显示所有任务的分类统计
    val filteredTasks = if (selectedPeriod != null) {
        filterTasksByPeriod(tasks, selectedPeriod, viewType)
    } else {
        tasks
    }

    if (filteredTasks.isEmpty()) return emptyMap()

    // 计算完成的任务的分类百分比
    val completedTasks = filteredTasks.filter { it.isCompleted }
    if (completedTasks.isEmpty()) return emptyMap()

    val categoryCount = mutableMapOf<String, Int>()
    completedTasks.forEach { task ->
        categoryCount[task.category] = categoryCount.getOrDefault(task.category, 0) + 1
    }

    val totalCompletedTasks = completedTasks.size
    return categoryCount.mapValues { (_, count) ->
        (count.toFloat() / totalCompletedTasks) * 100f
    }
}

@Composable
fun filterTasksByPeriod(tasks: List<Task>, period: String, viewType: StatisticsViewType): List<Task> {
    val calendar = Calendar.getInstance()

    return if (viewType == StatisticsViewType.MONTHLY) {
        // 解析月份格式 (例如: "Jan", "Feb")
        val monthNames = getMonthAbbreviations()
        val monthIndex = monthNames.indexOf(period)
        if (monthIndex == -1) return emptyList()

        tasks.filter { task ->
            val taskCalendar = Calendar.getInstance()
            taskCalendar.time = task.dueDate
            taskCalendar.get(Calendar.MONTH) == monthIndex &&
            taskCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
        }
    } else {
        // 解析年份格式
        val year = period.toIntOrNull() ?: return emptyList()

        tasks.filter { task ->
            val taskCalendar = Calendar.getInstance()
            taskCalendar.time = task.dueDate
            taskCalendar.get(Calendar.YEAR) == year
        }
    }
}

enum class StatisticsViewType {
    MONTHLY, YEARLY
}