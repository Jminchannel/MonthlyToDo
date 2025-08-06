package com.jmin.monthlytodo.ui.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onNavigateToThemeSettings: () -> Unit = {},
    onNavigateToLanguageSettings: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Appearance settings
        item {
            SettingsSection(
                title = stringResource(R.string.appearance_settings)
            ) {
                ThemeSetting(onNavigateToThemeSettings)
                Spacer(modifier = Modifier.height(8.dp))
                LanguageSetting(onNavigateToLanguageSettings)
            }
        }
        
        // Notifications settings
        item {
            SettingsSection(
                title = "Notifications"
            ) {
                SoundSetting()
            }
        }
        
        // About section
        item {
            SettingsSection(
                title = "About"
            ) {
                AppInfoItem()
                Spacer(modifier = Modifier.height(8.dp))
                RateAppItem()
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
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
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            content()
        }
    }
}

@Composable
fun ThemeSetting(onNavigateToThemeSettings: () -> Unit = {}) {
    SettingItem(
        icon = Icons.Default.Palette,
        title = stringResource(R.string.theme_settings),
        description = stringResource(R.string.theme_settings_desc),
        onClick = onNavigateToThemeSettings
    )
}

@Composable
fun LanguageSetting(
    onNavigateToLanguageSettings: () -> Unit
) {
    SettingItem(
        icon = Icons.Default.Language,
        title = stringResource(R.string.language_settings),
        description = stringResource(R.string.language),
        onClick = onNavigateToLanguageSettings
    )
}

@Composable
fun SoundSetting() {
    var isSoundEnabled by remember { mutableStateOf(true) }
    
    SettingItem(
        icon = Icons.Default.Notifications,
        title = "Sound",
        trailingContent = {
            Switch(
                checked = isSoundEnabled,
                onCheckedChange = { isSoundEnabled = it }
            )
        }
    )
}

@Composable
fun AppInfoItem() {
    var showAppAboutDialog by remember { mutableStateOf(false) }
    SettingItem(
        icon = Icons.Default.Info,
        title = "About MonthlyToDo",
        description = "Version 1.0.0",
        onClick = {
            showAppAboutDialog = true
        }
    )
    if (showAppAboutDialog) {
        AppInfoDialog(
            onDismiss = {
                showAppAboutDialog = false
            }
        )
    }
}
@Preview
@Composable
fun iwanttosee() {
    AppInfoDialog(
        onDismiss = {}
    )
}

@Composable
fun AppInfoDialog(onDismiss:() -> Unit){
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName,0).versionName
        } catch (e:Exception) {
            "unKnown"
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.about_us_ok))
            }
        },
        title = {
            Text(
                text = stringResource(R.string.about_us),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.version).plus(versionName),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.about_text_1),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.about_text_2),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        icon = {}
    )
}

@Composable
fun RateAppItem() {
    var showRatingDialog by remember { mutableStateOf(false) }
    
    SettingItem(
        icon = Icons.Default.Star,
        title = "Rate this app",
        onClick = {
            showRatingDialog = true
        }
    )
    
    if (showRatingDialog) {
        AlertDialog(
            onDismissRequest = { showRatingDialog = false },
            title = { Text(stringResource(R.string.rate_app_title)) },
            text = {
                Column {
                    Text(stringResource(R.string.rate_app_message))
                    Spacer(modifier = Modifier.height(16.dp))
                    // 这里可以添加评分组件
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // 打开应用商店评分页面
                        showRatingDialog = false
                    }
                ) {
                    Text(stringResource(R.string.rate_now))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showRatingDialog = false }
                ) {
                    Text(stringResource(R.string.later))
                }
            }
        )
    }
}

@Composable
fun SettingItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Trailing content
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(16.dp))
            trailingContent()
        }
        
        // Arrow for clickable items
        if (onClick != null && trailingContent == null) {
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}