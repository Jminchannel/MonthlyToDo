# 崩溃修复指南

## 🐛 崩溃问题

### 错误信息
```
java.lang.IllegalStateException: You must call setGraph() before calling getGraph()
at androidx.navigation.NavController.getGraph(NavController.kt:103)
```

### 问题原因
在 `SwipeableMainScreen` 中尝试访问 `navController.graph.startDestinationId`，但此时导航图（graph）还没有被设置。

---

## ✅ 修复方案

### 1. MainActivity.kt 修复

#### 问题代码：
```kotlin
LaunchedEffect(pagerState.currentPage) {
    currentPage = pagerState.currentPage
    navController.navigate(pages[currentPage]) {
        popUpTo(navController.graph.startDestinationId)  // ❌ 错误：graph还未设置
        launchSingleTop = true
    }
}

Scaffold(
    bottomBar = {
        BottomNavigationBar(
            navController = navController,
            onNavigate = { route ->
                val index = pages.indexOf(route)
                if (index != -1) {
                    GlobalScope.launch {  // ❌ 不推荐使用GlobalScope
                        pagerState.animateScrollToPage(index)
                    }
                }
            }
        )
    }
)
```

#### 修复后：
```kotlin
val scope = rememberCoroutineScope()  // ✅ 使用composable作用域

Scaffold(
    bottomBar = {
        BottomNavigationBar(
            navController = navController,
            currentRoute = pages[pagerState.currentPage],  // ✅ 直接传递当前路由
            onNavigate = { route ->
                val index = pages.indexOf(route)
                if (index != -1) {
                    scope.launch {  // ✅ 使用rememberCoroutineScope
                        pagerState.animateScrollToPage(index)
                    }
                }
            }
        )
    }
)
```

**关键改进：**
- ❌ 移除了对 `navController.graph` 的访问
- ✅ 直接通过 `pagerState.currentPage` 跟踪当前页面
- ✅ 使用 `rememberCoroutineScope` 替代 `GlobalScope`
- ✅ 通过参数传递当前路由到 `BottomNavigationBar`

---

### 2. BottomNavigationBar.kt 修复

#### 修改前：
```kotlin
@Composable
fun BottomNavigationBar(
    navController: NavController,
    onNavigate: ((String) -> Unit)? = null
) {
    // ...
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    items.forEach { item ->
        CustomNavigationItem(
            item = item,
            isSelected = currentRoute == item.route,  // 只能从NavController获取路由
            onClick = { /* ... */ }
        )
    }
}
```

#### 修复后：
```kotlin
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String? = null,  // ✅ 新增参数
    onNavigate: ((String) -> Unit)? = null
) {
    // ...
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val activeRoute = currentRoute ?: navBackStackEntry?.destination?.route  // ✅ 优先使用参数
    
    items.forEach { item ->
        CustomNavigationItem(
            item = item,
            isSelected = activeRoute == item.route,  // ✅ 使用activeRoute
            onClick = { /* ... */ }
        )
    }
}
```

**关键改进：**
- ✅ 添加 `currentRoute` 参数，支持外部传入当前路由
- ✅ 优先使用传入的 `currentRoute`，如果没有则从 `NavController` 获取
- ✅ 兼容两种使用方式（带HorizontalPager和不带）

---

## 📊 技术要点

### 为什么会崩溃？

在 Compose 中，`NavController` 需要先通过 `NavHost` 设置导航图（graph）才能使用。

```kotlin
// 错误流程：
1. 创建 NavController
2. ❌ 立即访问 navController.graph ← 此时graph还不存在
3. 设置 NavHost（这时才设置graph）

// 正确流程：
1. 创建 NavController
2. 设置 NavHost（设置graph）
3. ✅ 现在可以访问 navController.graph
```

在我们的情况下，`SwipeableMainScreen` 中使用 `HorizontalPager` 管理页面，**不需要访问 `NavController` 的 graph**，直接通过 `pagerState` 就能知道当前页面。

---

### HorizontalPager vs NavController

| 特性 | HorizontalPager | NavController |
|------|-----------------|---------------|
| 用途 | 左右滑动页面 | 导航栈管理 |
| 状态管理 | `pagerState.currentPage` | `currentBackStackEntry` |
| 适用场景 | 平级页面切换 | 层级导航 |
| 需要graph | ❌ 不需要 | ✅ 需要 |

在我们的应用中：
- **主页面（5个Tab）**: 使用 `HorizontalPager` ✅
- **子页面（主题设置等）**: 使用 `NavController` ✅

---

## 🎯 修复验证

### 运行测试：
1. ✅ 应用启动正常（不再崩溃）
2. ✅ 左右滑动切换页面正常
3. ✅ 点击底部导航图标正常
4. ✅ 底部导航高亮正确显示
5. ✅ 滑动和点击导航同步

### 预期效果：
```
✅ 启动应用 → 显示日历页面
✅ 向左滑动 → 切换到任务页面，底部导航高亮更新
✅ 点击统计图标 → 动画切换到统计页面
✅ 向右滑动 → 返回任务页面
✅ 所有页面切换流畅，无崩溃
```

---

## 🔧 关键代码片段

### MainActivity.kt - SwipeableMainScreen
```kotlin
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableMainScreen(
    navController: NavHostController,
    viewModel: TaskViewModel
) {
    val pages = listOf("calendar", "tasks", "statistics", "achievements", "settings")
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
    
    val scope = rememberCoroutineScope()  // ← 关键：使用composable作用域
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentRoute = pages[pagerState.currentPage],  // ← 关键：传递当前路由
                onNavigate = { route ->
                    val index = pages.indexOf(route)
                    if (index != -1) {
                        scope.launch {  // ← 关键：使用scope.launch
                            pagerState.animateScrollToPage(index)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            // 页面内容
        }
    }
}
```

---

## 📚 学到的经验

### 1. NavController 的正确使用
- ✅ **DO**: 在 `NavHost` 内部使用
- ❌ **DON'T**: 在设置 graph 之前访问 `navController.graph`

### 2. 协程作用域的选择
- ✅ **DO**: 在 Composable 中使用 `rememberCoroutineScope()`
- ❌ **DON'T**: 使用 `GlobalScope.launch`（生命周期不受控制）

### 3. 状态管理
- ✅ **DO**: 使用单一数据源（`pagerState.currentPage`）
- ❌ **DON'T**: 在多个地方重复管理状态

### 4. 组件通信
- ✅ **DO**: 通过参数传递状态和回调
- ❌ **DON'T**: 依赖全局状态或不稳定的引用

---

## 🎊 总结

### 崩溃原因
访问了未初始化的 `navController.graph`

### 解决方案
1. 移除对 `navController.graph` 的依赖
2. 使用 `pagerState` 直接管理页面状态
3. 通过参数传递当前路由到子组件
4. 使用 `rememberCoroutineScope` 替代 `GlobalScope`

### 结果
- ✅ 应用不再崩溃
- ✅ 滑动导航正常工作
- ✅ 代码更加简洁和可维护
- ✅ 遵循 Compose 最佳实践

---

*修复日期：2024年*  
*状态：已完全修复并测试*  
*可以正常运行了！🎉*


