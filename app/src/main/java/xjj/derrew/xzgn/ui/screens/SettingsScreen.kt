package xjj.derrew.xzgn.ui.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import xjj.derrew.xzgn.R
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
    onNavigateToLanguageSettings: () -> Unit = {},
    onNavigateToCalendarSizeSettings: () -> Unit = {},
    onNavigateToCategoryManagement: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)  // 自适应主题背景
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
                Spacer(modifier = Modifier.height(8.dp))
                CalendarSizeSetting(onNavigateToCalendarSizeSettings)
            }
        }
        
        // Task settings (新增)
        item {
            SettingsSection(
                title = stringResource(R.string.task_settings)
            ) {
                CategoryManagementSetting(onNavigateToCategoryManagement)
            }
        }
        
        // About section
        item {
            SettingsSection(
                title = stringResource(R.string.about_section)
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
    // 霓虹风格设置区域
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
fun CalendarSizeSetting(
    onNavigateToCalendarSizeSettings: () -> Unit
) {
    SettingItem(
        icon = Icons.Default.ZoomIn,
        title = stringResource(R.string.calendar_size_settings),
        description = stringResource(R.string.calendar_size_settings_desc),
        onClick = onNavigateToCalendarSizeSettings
    )
}

@Composable
fun CategoryManagementSetting(
    onNavigateToCategoryManagement: () -> Unit
) {
    SettingItem(
        icon = Icons.Default.Category,
        title = stringResource(R.string.category_management),
        description = stringResource(R.string.category_management_desc),
        onClick = onNavigateToCategoryManagement
    )
}

@Composable
fun AppInfoItem() {
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }
    var showAppAboutDialog by remember { mutableStateOf(false) }
    SettingItem(
        icon = Icons.Default.Info,
        title = stringResource(R.string.about_app_title),
        description = stringResource(R.string.version) + versionName,
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
fun AppInfoDialog(onDismiss: () -> Unit){
    val context = LocalContext.current
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Icon/Logo area
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                            contentDescription = stringResource(R.string.app_name),
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // App Name
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Version
                Text(
                    text = stringResource(R.string.version) + versionName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description
                Text(
                    text = stringResource(R.string.about_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Features Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.about_features_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        FeatureItem("✅", stringResource(R.string.about_feature_1))
                        FeatureItem("📅", stringResource(R.string.about_feature_2))
                        FeatureItem("📊", stringResource(R.string.about_feature_3))
                        FeatureItem("🏆", stringResource(R.string.about_feature_4))
                        FeatureItem("🎨", stringResource(R.string.about_feature_5))
                        FeatureItem("🌍", stringResource(R.string.about_feature_6))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Developer Info
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.about_developer_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = stringResource(R.string.about_developer_info),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Tech Stack
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.about_tech_stack),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Close Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        }
    }
}

@Composable
fun FeatureItem(icon: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun RateAppItem() {
    var showRatingDialog by remember { mutableStateOf(false) }
    
    SettingItem(
        icon = Icons.Default.Star,
        title = stringResource(R.string.rate_app_item_title),
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