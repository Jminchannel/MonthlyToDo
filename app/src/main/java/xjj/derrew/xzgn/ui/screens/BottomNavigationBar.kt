package xjj.derrew.xzgn.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import xjj.derrew.xzgn.R

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String? = null,  // 接受当前路由作为参数
    onNavigate: ((String) -> Unit)? = null
) {
    val items = listOf(
        NavigationItem.Calendar,
        NavigationItem.Tasks,
        NavigationItem.Statistics,
        NavigationItem.Settings
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,  // 自动适应主题
        tonalElevation = 3.dp,  // Material3标准高度
        shadowElevation = 8.dp,
        border = BorderStroke(
            1.dp, 
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)  // 主题色半透明边框
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 如果提供了currentRoute参数就使用它，否则从NavController获取
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val activeRoute = currentRoute ?: navBackStackEntry?.destination?.route

            items.forEach { item ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CustomNavigationItem(
                        item = item,
                        isSelected = activeRoute == item.route,
                        onClick = {
                            if (onNavigate != null) {
                                // 使用自定义导航（支持滑动）
                                onNavigate(item.route)
                            } else {
                                // 使用默认NavController导航
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomNavigationItem(
    item: NavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "iconScale"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = stringResource(item.title),
            modifier = Modifier
                .size(24.dp)
                .scale(iconScale),
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary  // 自动适应主题的主色
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)  // 自动适应的次要色
            }
        )

        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(animationSpec = tween(200)) + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                initialScale = 0.9f
            ),
            exit = fadeOut(animationSpec = tween(150)) + scaleOut(animationSpec = tween(150), targetScale = 0.9f)
        ) {
            Text(
                text = stringResource(item.title),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,  // 自动适应主题
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

sealed class NavigationItem(
    val route: String,
    val title: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Calendar : NavigationItem(
        "calendar",
        R.string.calendar,
        Icons.Default.CalendarToday
    )
    
    object Tasks : NavigationItem(
        "tasks",
        R.string.tasks,
        Icons.Default.List
    )
    
    object Statistics : NavigationItem(
        "statistics",
        R.string.statistics,
        Icons.Default.ShowChart
    )
    
    object Settings : NavigationItem(
        "settings",
        R.string.settings,
        Icons.Default.Settings
    )
}