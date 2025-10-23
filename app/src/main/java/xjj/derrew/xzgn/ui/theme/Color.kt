package xjj.derrew.xzgn.ui.theme

import androidx.compose.ui.graphics.Color

// ===== 深色霓虹赛博朋克风格配色 =====

// 主色 - 霓虹青色
val NeonCyan = Color(0xFF06B6D4)
val NeonCyanLight = Color(0xFF22D3EE)
val NeonCyanDark = Color(0xFF0891B2)

// 辅助色 - 霓虹紫色
val NeonPurple = Color(0xFFC026D3)
val NeonPurpleLight = Color(0xFFD946EF)
val NeonPurpleDark = Color(0xFFA21CAF)

// 霓虹粉
val NeonPink = Color(0xFFEC4899)

// 霓虹绿
val NeonGreen = Color(0xFF10B981)

// 霓虹橙
val NeonOrange = Color(0xFFF59E0B)

// 背景色 - 深黑灰渐变
val DarkBg1 = Color(0xFF0F172A) // 最深背景
val DarkBg2 = Color(0xFF1E293B) // 中等背景
val DarkBg3 = Color(0xFF334155) // 表面色

// 文字颜色
val TextPrimary = Color(0xFFF8FAFC)    // 主要文字
val TextSecondary = Color(0xFFCBD5E1)  // 次要文字
val TextTertiary = Color(0xFF94A3B8)   // 第三级文字

// 优先级颜色（霓虹风格）
val PriorityHigh = Color(0xFFEF4444)
val PriorityMedium = Color(0xFFF59E0B)
val PriorityLow = Color(0xFF10B981)

// 热力图颜色（霓虹青色系）
val Heatmap0 = Color(0xFF1E293B)
val Heatmap1 = Color(0xFF0C4A6E)
val Heatmap2 = Color(0xFF075985)
val Heatmap3 = Color(0xFF0369A1)
val Heatmap4 = Color(0xFF0891B2)
val Heatmap5 = Color(0xFF06B6D4)
val Heatmap6 = Color(0xFF22D3EE)

// 节日颜色
val Holiday = Color(0xFFEC4899)

// 兼容性：保留旧变量名但使用新颜色
val Purple80 = NeonPurpleLight
val PurpleGrey80 = Color(0xFF94A3B8)
val Pink80 = NeonPink

val Purple40 = NeonPurple
val PurpleGrey40 = DarkBg3
val Pink40 = NeonPink

val Primary = NeonCyan           // 主色改为霓虹青
val PrimaryDark = NeonCyanDark
val Secondary = NeonPurple       // 辅助色改为霓虹紫
val Background = DarkBg1         // 背景改为深黑
val Surface = DarkBg2            // 表面色改为深灰
val Error = Color(0xFFEF4444)