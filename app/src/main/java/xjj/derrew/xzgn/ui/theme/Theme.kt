package xjj.derrew.xzgn.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 深色霓虹配色方案（新默认）
private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DarkBg1,
    primaryContainer = NeonCyanDark,
    onPrimaryContainer = NeonCyanLight,
    
    secondary = NeonPurple,
    onSecondary = DarkBg1,
    secondaryContainer = NeonPurpleDark,
    onSecondaryContainer = NeonPurpleLight,
    
    tertiary = NeonPink,
    onTertiary = DarkBg1,
    
    background = DarkBg1,           // 深黑背景
    onBackground = TextPrimary,
    
    surface = DarkBg2,              // 深灰表面
    onSurface = TextPrimary,
    surfaceVariant = DarkBg3,
    onSurfaceVariant = TextSecondary,
    
    outline = DarkBg3,
    error = Error,
    onError = Color.White
)

// 浅色配色方案（优化后更美观）
private val LightColorScheme = lightColorScheme(
    primary = NeonCyan,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFFAFE),  // 浅青色容器
    onPrimaryContainer = Color(0xFF083344),
    
    secondary = NeonPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF3E5F5),  // 浅紫色容器
    onSecondaryContainer = Color(0xFF4A148C),
    
    tertiary = NeonPink,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0F0),
    
    background = Color(0xFFF5F9FA),        // 柔和浅色背景
    onBackground = Color(0xFF1A1C1E),
    
    surface = Color.White,                 // 白色卡片
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE7F5F8),    // 浅青色变体
    onSurfaceVariant = Color(0xFF40484C),
    
    outline = Color(0xFFCDD5D9),
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFE8E6)
)

@Composable
fun MonthlyQuestJournalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // 默认关闭动态颜色，使用自定义主题
    content: @Composable () -> Unit
) {
    val themeConfig = rememberThemeConfig()

    // 根据主题模式确定是否使用深色主题
    val isDarkTheme = when (themeConfig.mode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> darkTheme
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (isDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> themeConfig.theme.getColorScheme(isDarkTheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}