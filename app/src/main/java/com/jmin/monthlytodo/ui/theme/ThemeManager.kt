package com.jmin.monthlytodo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// 主题类型枚举
enum class AppTheme {
    BLUE,
    GREEN,
    PURPLE,
    ORANGE,
    PINK
}

// 主题模式枚举
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

// 主题数据类
data class ThemeConfig(
    val theme: AppTheme = AppTheme.BLUE,
    val mode: ThemeMode = ThemeMode.SYSTEM
)

// 主题管理器
object ThemeManager {
    private val _themeConfig = MutableStateFlow(ThemeConfig())
    val themeConfig: StateFlow<ThemeConfig> = _themeConfig
    
    fun updateTheme(theme: AppTheme) {
        _themeConfig.value = _themeConfig.value.copy(theme = theme)
    }
    
    fun updateMode(mode: ThemeMode) {
        _themeConfig.value = _themeConfig.value.copy(mode = mode)
    }
    
    fun updateConfig(config: ThemeConfig) {
        _themeConfig.value = config
    }
}

// 主题颜色定义
object ThemeColors {
    // 蓝色主题
    val BlueLight = lightColorScheme(
        primary = Color(0xFF4361EE),
        onPrimary = Color.White,
        secondary = Color(0xFF4CC9F0),
        background = Color(0xFFF0F4F8),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )
    
    val BlueDark = darkColorScheme(
        primary = Color(0xFF6B73FF),
        onPrimary = Color.White,
        secondary = Color(0xFF4CC9F0),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onBackground = Color(0xFFE1E1E1),
        onSurface = Color(0xFFE1E1E1)
    )
    
    // 绿色主题
    val GreenLight = lightColorScheme(
        primary = Color(0xFF10B981),
        onPrimary = Color.White,
        secondary = Color(0xFF34D399),
        background = Color(0xFFF0FDF4),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )
    
    val GreenDark = darkColorScheme(
        primary = Color(0xFF34D399),
        onPrimary = Color.Black,
        secondary = Color(0xFF6EE7B7),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onBackground = Color(0xFFE1E1E1),
        onSurface = Color(0xFFE1E1E1)
    )
    
    // 紫色主题
    val PurpleLight = lightColorScheme(
        primary = Color(0xFF8B5CF6),
        onPrimary = Color.White,
        secondary = Color(0xFFA78BFA),
        background = Color(0xFFFAF5FF),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )
    
    val PurpleDark = darkColorScheme(
        primary = Color(0xFFA78BFA),
        onPrimary = Color.Black,
        secondary = Color(0xFFC4B5FD),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onBackground = Color(0xFFE1E1E1),
        onSurface = Color(0xFFE1E1E1)
    )
    
    // 橙色主题
    val OrangeLight = lightColorScheme(
        primary = Color(0xFFF59E0B),
        onPrimary = Color.White,
        secondary = Color(0xFFFBBF24),
        background = Color(0xFFFFFBEB),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )
    
    val OrangeDark = darkColorScheme(
        primary = Color(0xFFFBBF24),
        onPrimary = Color.Black,
        secondary = Color(0xFFFDE047),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onBackground = Color(0xFFE1E1E1),
        onSurface = Color(0xFFE1E1E1)
    )
    
    // 粉色主题
    val PinkLight = lightColorScheme(
        primary = Color(0xFFEC4899),
        onPrimary = Color.White,
        secondary = Color(0xFFF472B6),
        background = Color(0xFFFDF2F8),
        surface = Color(0xFFFFFFFF),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F)
    )
    
    val PinkDark = darkColorScheme(
        primary = Color(0xFFF472B6),
        onPrimary = Color.Black,
        secondary = Color(0xFFF9A8D4),
        background = Color(0xFF121212),
        surface = Color(0xFF1E1E1E),
        onBackground = Color(0xFFE1E1E1),
        onSurface = Color(0xFFE1E1E1)
    )
}

// 获取颜色方案的扩展函数
fun AppTheme.getColorScheme(isDark: Boolean): ColorScheme {
    return when (this) {
        AppTheme.BLUE -> if (isDark) ThemeColors.BlueDark else ThemeColors.BlueLight
        AppTheme.GREEN -> if (isDark) ThemeColors.GreenDark else ThemeColors.GreenLight
        AppTheme.PURPLE -> if (isDark) ThemeColors.PurpleDark else ThemeColors.PurpleLight
        AppTheme.ORANGE -> if (isDark) ThemeColors.OrangeDark else ThemeColors.OrangeLight
        AppTheme.PINK -> if (isDark) ThemeColors.PinkDark else ThemeColors.PinkLight
    }
}

// Composable函数获取当前主题配置
@Composable
fun rememberThemeConfig(): ThemeConfig {
    val config by ThemeManager.themeConfig.collectAsState()
    return config
}
