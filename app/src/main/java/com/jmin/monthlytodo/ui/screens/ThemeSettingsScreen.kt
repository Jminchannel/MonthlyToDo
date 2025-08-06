package com.jmin.monthlytodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.jmin.monthlytodo.R
import com.jmin.monthlytodo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    onNavigateBack: () -> Unit
) {
    val themeConfig = rememberThemeConfig()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.theme_settings)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 主题模式选择
            item {
                ThemeModeSection(
                    currentMode = themeConfig.mode,
                    onModeChange = { ThemeManager.updateMode(it) }
                )
            }
            
            // 主题颜色选择
            item {
                ThemeColorSection(
                    currentTheme = themeConfig.theme,
                    onThemeChange = { ThemeManager.updateTheme(it) }
                )
            }
        }
    }
}

@Composable
fun ThemeModeSection(
    currentMode: ThemeMode,
    onModeChange: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.display_mode),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ThemeMode.values().forEach { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onModeChange(mode) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentMode == mode,
                        onClick = { onModeChange(mode) }
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        imageVector = when (mode) {
                            ThemeMode.LIGHT -> Icons.Default.LightMode
                            ThemeMode.DARK -> Icons.Default.DarkMode
                            ThemeMode.SYSTEM -> Icons.Default.Settings
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = getThemeModeDisplayName(mode),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeColorSection(
    currentTheme: AppTheme,
    onThemeChange: (AppTheme) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.theme_color),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 颜色选择网格
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AppTheme.values().forEach { theme ->
                    ThemeColorItem(
                        theme = theme,
                        isSelected = currentTheme == theme,
                        onClick = { onThemeChange(theme) }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeColorItem(
    theme: AppTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val primaryColor = theme.getColorScheme(false).primary
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(primaryColor)
                .then(
                    if (isSelected) {
                        Modifier.border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape
                        )
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = getAppThemeDisplayName(theme),
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun getThemeModeDisplayName(mode: ThemeMode): String {
    return when (mode) {
        ThemeMode.LIGHT -> stringResource(R.string.light_mode)
        ThemeMode.DARK -> stringResource(R.string.dark_mode)
        ThemeMode.SYSTEM -> stringResource(R.string.follow_system)
    }
}

@Composable
fun getAppThemeDisplayName(theme: AppTheme): String {
    return when (theme) {
        AppTheme.BLUE -> stringResource(R.string.blue_theme)
        AppTheme.GREEN -> stringResource(R.string.green_theme)
        AppTheme.PURPLE -> stringResource(R.string.purple_theme)
        AppTheme.ORANGE -> stringResource(R.string.orange_theme)
        AppTheme.PINK -> stringResource(R.string.pink_theme)
    }
}
