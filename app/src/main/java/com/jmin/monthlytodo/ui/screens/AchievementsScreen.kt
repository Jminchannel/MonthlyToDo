package com.jmin.monthlytodo.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jmin.monthlytodo.R
import com.jmin.monthlytodo.model.Task
import com.jmin.monthlytodo.viewmodel.TaskViewModel
import java.util.*

@Composable
fun AchievementsScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()

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
                text = stringResource(R.string.achievements),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = { /* TODO: Implement share all */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = stringResource(R.string.share_all),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Achievement badges
        AchievementBadges(tasks)

        Spacer(modifier = Modifier.height(24.dp))

        // Recent milestones
        RecentMilestones(tasks)
    }
}

@Composable
fun AchievementBadges(tasks: List<Task>) {
    val achievements = getAchievements(tasks)

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(achievements) { achievement ->
            AchievementBadge(achievement = achievement)
        }
    }
}

@Composable
fun AchievementBadge(achievement: Achievement) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (achievement.isUnlocked) {
                            achievement.gradientColors
                        } else {
                            listOf(Color.Gray.copy(alpha = 0.3f), Color.Gray.copy(alpha = 0.5f))
                        }
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = achievement.icon,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(8.dp),
                    tint = if (achievement.isUnlocked) Color.White else Color.Gray
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = achievement.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.isUnlocked) Color.White else Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (achievement.isUnlocked) Color.White.copy(alpha = 0.9f) else Color.Gray.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (achievement.isUnlocked) {
                    Button(
                        onClick = { /* TODO: Implement share */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f),
                            contentColor = Color.White
                        ),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.share),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.locked),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        modifier = Modifier
                            .background(
                                Color.Gray.copy(alpha = 0.2f),
                                RoundedCornerShape(15.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RecentMilestones(tasks: List<Task>) {
    val milestones = getMilestones()

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
                text = stringResource(R.string.recent_milestones),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(milestones.size) { index ->
                    MilestoneItem(milestone = milestones[index])
                }
            }
        }
    }
}

@Composable
fun MilestoneItem(milestone: Milestone) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(milestone.iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = milestone.icon,
                contentDescription = null,
                tint = milestone.iconTint,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = milestone.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = milestone.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Time
        Text(
            text = milestone.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class Achievement(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val gradientColors: List<Color>,
    val isUnlocked: Boolean
)

data class Milestone(
    val title: String,
    val description: String,
    val time: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val iconBackground: Color,
    val iconTint: Color
)

@Composable
fun getAchievements(tasks: List<Task>): List<Achievement> {
    val currentMonth = Calendar.getInstance()
    val monthlyTasks = tasks.filter { task ->
        val taskCalendar = Calendar.getInstance()
        taskCalendar.time = task.dueDate
        taskCalendar.get(Calendar.MONTH) == currentMonth.get(Calendar.MONTH) &&
        taskCalendar.get(Calendar.YEAR) == currentMonth.get(Calendar.YEAR)
    }

    val completedTasks = monthlyTasks.filter { it.isCompleted }
    val totalTasks = monthlyTasks.size
    val completionRate = if (totalTasks > 0) (completedTasks.size.toFloat() / totalTasks) * 100 else 0f

    // 计算连续完成天数
    val consecutiveDays = calculateConsecutiveDays(tasks)

    // 计算高优先级任务完成数
    val highPriorityCompleted = completedTasks.count { it.priority == com.jmin.monthlytodo.model.Priority.HIGH }

    // 计算本月任务数量
    val monthlyTaskCount = monthlyTasks.size

    return listOf(
        Achievement(
            title = stringResource(R.string.achievement_task_master),
            description = stringResource(R.string.achievement_task_master_desc),
            icon = Icons.Default.CheckCircle,
            gradientColors = listOf(Color(0xFF81C784), Color(0xFF4CAF50)),
            isUnlocked = completedTasks.size >= 10
        ),
        Achievement(
            title = stringResource(R.string.achievement_consistency_king),
            description = stringResource(R.string.achievement_consistency_king_desc),
            icon = Icons.Default.Whatshot,
            gradientColors = listOf(Color(0xFFFFB74D), Color(0xFFFF9800)),
            isUnlocked = consecutiveDays >= 7
        ),
        Achievement(
            title = stringResource(R.string.achievement_high_achiever),
            description = stringResource(R.string.achievement_high_achiever_desc),
            icon = Icons.Default.Star,
            gradientColors = listOf(Color(0xFFE57373), Color(0xFFF44336)),
            isUnlocked = highPriorityCompleted >= 5
        ),
        Achievement(
            title = stringResource(R.string.achievement_productive_month),
            description = stringResource(R.string.achievement_productive_month_desc),
            icon = Icons.Default.Assignment,
            gradientColors = listOf(Color(0xFF64B5F6), Color(0xFF2196F3)),
            isUnlocked = monthlyTaskCount >= 20
        ),
        Achievement(
            title = stringResource(R.string.achievement_perfectionist),
            description = stringResource(R.string.achievement_perfectionist_desc),
            icon = Icons.Default.EmojiEvents,
            gradientColors = listOf(Color(0xFFBA68C8), Color(0xFF9C27B0)),
            isUnlocked = completionRate >= 80f
        ),
        Achievement(
            title = stringResource(R.string.achievement_early_starter),
            description = stringResource(R.string.achievement_early_starter_desc),
            icon = Icons.Default.Speed,
            gradientColors = listOf(Color(0xFF4DB6AC), Color(0xFF009688)),
            isUnlocked = completedTasks.size >= 15
        )
    )
}

fun calculateConsecutiveDays(tasks: List<Task>): Int {
    val completedTasks = tasks.filter { it.isCompleted }
    if (completedTasks.isEmpty()) return 0

    // 按日期分组已完成的任务
    val tasksByDate = completedTasks.groupBy { task ->
        val calendar = Calendar.getInstance()
        calendar.time = task.dueDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.time
    }

    val sortedDates = tasksByDate.keys.sorted().reversed() // 从最近的日期开始
    if (sortedDates.isEmpty()) return 0

    var consecutiveDays = 0
    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)

    for (i in sortedDates.indices) {
        val expectedDate = Calendar.getInstance()
        expectedDate.time = today.time
        expectedDate.add(Calendar.DAY_OF_MONTH, -i)

        if (sortedDates[i].time == expectedDate.timeInMillis) {
            consecutiveDays++
        } else {
            break
        }
    }

    return consecutiveDays
}

@Composable
fun getMilestones(): List<Milestone> {
    return listOf(
        Milestone(
            title = stringResource(R.string.milestone_tasks_completed),
            description = stringResource(R.string.milestone_june_2023),
            time = stringResource(R.string.milestone_2_days_ago),
            icon = Icons.Default.Event,
            iconBackground = Color(0xFFE3F2FD),
            iconTint = Color(0xFF2196F3)
        ),
        Milestone(
            title = stringResource(R.string.milestone_hours_focused),
            description = stringResource(R.string.milestone_across_tasks),
            time = stringResource(R.string.milestone_1_week_ago),
            icon = Icons.Default.Schedule,
            iconBackground = Color(0xFFE8F5E9),
            iconTint = Color(0xFF4CAF50)
        )
    )
}