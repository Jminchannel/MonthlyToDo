package xjj.derrew.xzgn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import xjj.derrew.xzgn.R
import xjj.derrew.xzgn.manager.CalendarSettingsManager

/**
 * 日历大小设置页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarSizeSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val calendarSettingsManager = remember { CalendarSettingsManager.getInstance(context) }
    val scope = rememberCoroutineScope()
    
    val currentSize by calendarSettingsManager.getCalendarSize().collectAsState(initial = CalendarSettingsManager.SIZE_MEDIUM)
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.calendar_size_settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 提示信息
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.calendar_size_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // 小尺寸
            item {
                CalendarSizeOption(
                    title = stringResource(R.string.calendar_size_small),
                    description = stringResource(R.string.calendar_size_small_desc),
                    size = CalendarSettingsManager.SIZE_SMALL,
                    currentSize = currentSize,
                    onClick = {
                        scope.launch {
                            calendarSettingsManager.setCalendarSize(CalendarSettingsManager.SIZE_SMALL)
                        }
                    }
                )
            }
            
            // 中等尺寸
            item {
                CalendarSizeOption(
                    title = stringResource(R.string.calendar_size_medium),
                    description = stringResource(R.string.calendar_size_medium_desc),
                    size = CalendarSettingsManager.SIZE_MEDIUM,
                    currentSize = currentSize,
                    onClick = {
                        scope.launch {
                            calendarSettingsManager.setCalendarSize(CalendarSettingsManager.SIZE_MEDIUM)
                        }
                    }
                )
            }
            
            // 大尺寸
            item {
                CalendarSizeOption(
                    title = stringResource(R.string.calendar_size_large),
                    description = stringResource(R.string.calendar_size_large_desc),
                    size = CalendarSettingsManager.SIZE_LARGE,
                    currentSize = currentSize,
                    onClick = {
                        scope.launch {
                            calendarSettingsManager.setCalendarSize(CalendarSettingsManager.SIZE_LARGE)
                        }
                    }
                )
            }
            
            // 特大尺寸
            item {
                CalendarSizeOption(
                    title = stringResource(R.string.calendar_size_extra_large),
                    description = stringResource(R.string.calendar_size_extra_large_desc),
                    size = CalendarSettingsManager.SIZE_EXTRA_LARGE,
                    currentSize = currentSize,
                    onClick = {
                        scope.launch {
                            calendarSettingsManager.setCalendarSize(CalendarSettingsManager.SIZE_EXTRA_LARGE)
                        }
                    }
                )
            }
        }
    }
}

/**
 * 日历大小选项
 */
@Composable
fun CalendarSizeOption(
    title: String,
    description: String,
    size: Float,
    currentSize: Float,
    onClick: () -> Unit
) {
    val isSelected = size == currentSize
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

