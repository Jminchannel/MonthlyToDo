package xjj.derrew.xzgn.ui.theme

import android.content.Context
import android.content.SharedPreferences
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
    private const val PREFS_NAME = "theme_preferences"
    private const val KEY_THEME = "app_theme"
    private const val KEY_MODE = "theme_mode"
    
    private var prefs: SharedPreferences? = null
    private val _themeConfig = MutableStateFlow(ThemeConfig())
    val themeConfig: StateFlow<ThemeConfig> = _themeConfig
    
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadThemeConfig()
    }
    
    private fun loadThemeConfig() {
        val themeName = prefs?.getString(KEY_THEME, AppTheme.BLUE.name) ?: AppTheme.BLUE.name
        val modeName = prefs?.getString(KEY_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        
        val theme = try {
            AppTheme.valueOf(themeName)
        } catch (e: Exception) {
            AppTheme.BLUE
        }
        
        val mode = try {
            ThemeMode.valueOf(modeName)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
        
        _themeConfig.value = ThemeConfig(theme, mode)
    }
    
    fun updateTheme(theme: AppTheme) {
        _themeConfig.value = _themeConfig.value.copy(theme = theme)
        prefs?.edit()?.putString(KEY_THEME, theme.name)?.apply()
    }
    
    fun updateMode(mode: ThemeMode) {
        _themeConfig.value = _themeConfig.value.copy(mode = mode)
        prefs?.edit()?.putString(KEY_MODE, mode.name)?.apply()
    }
    
    fun updateConfig(config: ThemeConfig) {
        _themeConfig.value = config
        prefs?.edit()?.apply {
            putString(KEY_THEME, config.theme.name)
            putString(KEY_MODE, config.mode.name)
            apply()
        }
    }
}

// 主题颜色定义 - 霓虹风格
object ThemeColors {
    // 霓虹青主题（默认）
    val BlueLight = lightColorScheme(
        primary = Color(0xFF06B6D4),          // 霓虹青
        onPrimary = Color.White,
        primaryContainer = Color(0xFFCFFAFE),
        onPrimaryContainer = Color(0xFF083344),
        secondary = Color(0xFFC026D3),        // 霓虹紫
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFF3E5F5),
        background = Color(0xFFF5F9FA),
        onBackground = Color(0xFF1A1C1E),
        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFFE7F5F8),
        onSurfaceVariant = Color(0xFF40484C),
        outline = Color(0xFFCDD5D9),
        error = Color(0xFFEF4444)
    )
    
    val BlueDark = darkColorScheme(
        primary = Color(0xFF06B6D4),          // 霓虹青
        onPrimary = Color(0xFF0F172A),
        primaryContainer = Color(0xFF0891B2),
        onPrimaryContainer = Color(0xFF22D3EE),
        secondary = Color(0xFFC026D3),        // 霓虹紫
        onSecondary = Color(0xFF0F172A),
        secondaryContainer = Color(0xFFA21CAF),
        onSecondaryContainer = Color(0xFFD946EF),
        background = Color(0xFF0F172A),       // 深黑
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF1E293B),          // 深灰
        onSurface = Color(0xFFF8FAFC),
        surfaceVariant = Color(0xFF334155),
        onSurfaceVariant = Color(0xFFCBD5E1),
        outline = Color(0xFF475569),
        error = Color(0xFFEF4444)
    )
    
    // 霓虹绿主题
    val GreenLight = lightColorScheme(
        primary = Color(0xFF10B981),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD1FAE5),
        onPrimaryContainer = Color(0xFF064E3B),
        secondary = Color(0xFF34D399),
        background = Color(0xFFF0FDF4),
        onBackground = Color(0xFF1A1C1E),
        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFFD1FAE5),
        error = Color(0xFFEF4444)
    )
    
    val GreenDark = darkColorScheme(
        primary = Color(0xFF10B981),
        onPrimary = Color(0xFF0F172A),
        primaryContainer = Color(0xFF047857),
        onPrimaryContainer = Color(0xFF34D399),
        secondary = Color(0xFF34D399),
        background = Color(0xFF0F172A),
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF1E293B),
        onSurface = Color(0xFFF8FAFC),
        surfaceVariant = Color(0xFF334155),
        error = Color(0xFFEF4444)
    )
    
    // 霓虹紫主题
    val PurpleLight = lightColorScheme(
        primary = Color(0xFFC026D3),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFF3E5F5),
        onPrimaryContainer = Color(0xFF4A148C),
        secondary = Color(0xFFD946EF),
        background = Color(0xFFFAF5FF),
        onBackground = Color(0xFF1A1C1E),
        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFFF3E5F5),
        error = Color(0xFFEF4444)
    )
    
    val PurpleDark = darkColorScheme(
        primary = Color(0xFFC026D3),
        onPrimary = Color(0xFF0F172A),
        primaryContainer = Color(0xFFA21CAF),
        onPrimaryContainer = Color(0xFFD946EF),
        secondary = Color(0xFFD946EF),
        background = Color(0xFF0F172A),
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF1E293B),
        onSurface = Color(0xFFF8FAFC),
        surfaceVariant = Color(0xFF334155),
        error = Color(0xFFEF4444)
    )
    
    // 霓虹橙主题
    val OrangeLight = lightColorScheme(
        primary = Color(0xFFF59E0B),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFEF3C7),
        onPrimaryContainer = Color(0xFF78350F),
        secondary = Color(0xFFFBBF24),
        background = Color(0xFFFFFBEB),
        onBackground = Color(0xFF1A1C1E),
        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFFFEF3C7),
        error = Color(0xFFEF4444)
    )
    
    val OrangeDark = darkColorScheme(
        primary = Color(0xFFF59E0B),
        onPrimary = Color(0xFF0F172A),
        primaryContainer = Color(0xFFD97706),
        onPrimaryContainer = Color(0xFFFBBF24),
        secondary = Color(0xFFFBBF24),
        background = Color(0xFF0F172A),
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF1E293B),
        onSurface = Color(0xFFF8FAFC),
        surfaceVariant = Color(0xFF334155),
        error = Color(0xFFEF4444)
    )
    
    // 霓虹粉主题
    val PinkLight = lightColorScheme(
        primary = Color(0xFFEC4899),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFFFE0F0),
        onPrimaryContainer = Color(0xFF831843),
        secondary = Color(0xFFF472B6),
        background = Color(0xFFFDF2F8),
        onBackground = Color(0xFF1A1C1E),
        surface = Color.White,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFFFFE0F0),
        error = Color(0xFFEF4444)
    )
    
    val PinkDark = darkColorScheme(
        primary = Color(0xFFEC4899),
        onPrimary = Color(0xFF0F172A),
        primaryContainer = Color(0xFFDB2777),
        onPrimaryContainer = Color(0xFFF472B6),
        secondary = Color(0xFFF472B6),
        background = Color(0xFF0F172A),
        onBackground = Color(0xFFF8FAFC),
        surface = Color(0xFF1E293B),
        onSurface = Color(0xFFF8FAFC),
        surfaceVariant = Color(0xFF334155),
        error = Color(0xFFEF4444)
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
