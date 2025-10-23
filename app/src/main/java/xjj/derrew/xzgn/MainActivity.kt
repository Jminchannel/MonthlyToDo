package xjj.derrew.xzgn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import xjj.derrew.xzgn.database.AppDatabase
import xjj.derrew.xzgn.repository.TaskRepository
import xjj.derrew.xzgn.ui.screens.BottomNavigationBar
import xjj.derrew.xzgn.ui.screens.CalendarScreen
import xjj.derrew.xzgn.ui.screens.SettingsScreen
import xjj.derrew.xzgn.ui.screens.StatisticsScreen
import xjj.derrew.xzgn.ui.screens.TaskListScreen
import xjj.derrew.xzgn.ui.screens.ThemeSettingsScreen
import xjj.derrew.xzgn.ui.screens.LanguageSettingsScreen
import xjj.derrew.xzgn.ui.screens.CalendarSizeSettingsScreen
import xjj.derrew.xzgn.ui.screens.CategoryManagementScreen
import xjj.derrew.xzgn.ui.theme.MonthlyQuestJournalTheme
import xjj.derrew.xzgn.viewmodel.TaskViewModel
import xjj.derrew.xzgn.manager.LanguageManager
import xjj.derrew.xzgn.model.Holiday
import xjj.derrew.xzgn.model.Task
import xjj.derrew.xzgn.viewmodel.DeviceViewModel

class MainActivity : ComponentActivity() {
    private val deviceViewModel: DeviceViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize language manager
        LanguageManager.initialize(this)
        LanguageManager.applyLanguage(this, LanguageManager.getCurrentLanguage())
        
        // Initialize theme manager
        xjj.derrew.xzgn.ui.theme.ThemeManager.initialize(this)

        enableEdgeToEdge()
        
        // Initialize database and repository here, outside of any Composable functions
        val database = AppDatabase.getDatabase(this)
        val repository = TaskRepository(database.taskDao(), database.holidayDao())
        val viewModelFactory = TaskViewModelFactory(repository)
        val viewModel: TaskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
        
        setContent {
            MonthlyQuestJournalTheme { // 自动跟随系统主题，支持切换
                LaunchedEffect(Unit) {
                    deviceViewModel.recordDeviceInfo(this@MainActivity)
                }
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun MainScreen(viewModel: TaskViewModel) {
    val navController = rememberNavController()
    
    // 支持左右滑动切换的主屏幕
    SwipeableMainScreen(
        navController = navController,
        viewModel = viewModel
    )
}

// 支持左右滑动的主屏幕
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeableMainScreen(
    navController: NavHostController,
    viewModel: TaskViewModel
) {
    val pages = listOf("calendar", "tasks", "statistics", "settings")
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
    
    // 使用 rememberCoroutineScope 来处理页面切换
    val scope = rememberCoroutineScope()
    
    // 状态管理设置子页面的导航
    var showThemeSettings by remember { mutableStateOf(false) }
    var showLanguageSettings by remember { mutableStateOf(false) }
    var showCalendarSizeSettings by remember { mutableStateOf(false) }
    var showCategoryManagement by remember { mutableStateOf(false) }
    
    if (showThemeSettings) {
        // 显示主题设置页面
        ThemeSettingsScreen(
            onNavigateBack = {
                showThemeSettings = false
            }
        )
    } else if (showLanguageSettings) {
        // 显示语言设置页面
        LanguageSettingsScreen(
            onNavigateBack = {
                showLanguageSettings = false
            }
        )
    } else if (showCalendarSizeSettings) {
        // 显示日历大小设置页面
        CalendarSizeSettingsScreen(
            onNavigateBack = {
                showCalendarSizeSettings = false
            }
        )
    } else if (showCategoryManagement) {
        // 显示分类管理页面
        CategoryManagementScreen(
            taskViewModel = viewModel,
            onNavigateBack = {
                showCategoryManagement = false
            }
        )
    } else {
        // 底部导航栏动画状态
        var isBottomBarVisible by remember { mutableStateOf(false) }
        
        // 启动底部导航栏动画
        LaunchedEffect(Unit) {
            isBottomBarVisible = true
        }
        
        // 底部导航栏动画
        val bottomBarOffsetY by animateFloatAsState(
            targetValue = if (isBottomBarVisible) 0f else 100f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "bottomBarAnimation"
        )
        
        val bottomBarAlpha by animateFloatAsState(
            targetValue = if (isBottomBarVisible) 1f else 0f,
            animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            label = "bottomBarAlpha"
        )
        
        // 显示主界面
        Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = bottomBarOffsetY
                        alpha = bottomBarAlpha
                    }
            ) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = pages[pagerState.currentPage],  // 传递当前路由
                    onNavigate = { route ->
                        val index = pages.indexOf(route)
                        if (index != -1) {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        // HorizontalPager - 包含所有页面
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (pages[page]) {
                "calendar" -> CalendarWithTaskList(viewModel)  // 日历和任务列表整合在一起，可滚动
                "tasks" -> TaskListScreen(viewModel)
                "statistics" -> StatisticsScreen(viewModel)
                "settings" -> SettingsScreen(
                    onNavigateToThemeSettings = { showThemeSettings = true },
                    onNavigateToLanguageSettings = { showLanguageSettings = true },
                    onNavigateToCalendarSizeSettings = { showCalendarSizeSettings = true },
                    onNavigateToCategoryManagement = { showCategoryManagement = true }
                )
            }
        }
    }
    }
}

// 日历和任务列表整合组件（可整体滚动）
@Composable
fun CalendarWithTaskList(viewModel: TaskViewModel) {
    val context = LocalContext.current
    val calendarSettingsManager = remember { xjj.derrew.xzgn.manager.CalendarSettingsManager.getInstance(context) }
    val calendarSize by calendarSettingsManager.getCalendarSize().collectAsState(initial = xjj.derrew.xzgn.manager.CalendarSettingsManager.SIZE_MEDIUM)
    
    // 日历头部动画状态
    var isHeaderVisible by remember { mutableStateOf(false) }
    
    // 启动日历头部动画
    LaunchedEffect(Unit) {
        isHeaderVisible = true
    }
    
    // 日历头部动画
    val headerOffsetY by animateFloatAsState(
        targetValue = if (isHeaderVisible) 0f else -150f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "headerAnimation"
    )
    
    val headerAlpha by animateFloatAsState(
        targetValue = if (isHeaderVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "headerAlpha"
    )
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 日历头部（带动画）
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = headerOffsetY
                        alpha = headerAlpha
                    }
            ) {
                CalendarHeader(
                    viewModel = viewModel,
                    calendarSize = calendarSize,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // 当月任务列表
        item {
            CalendarTaskListContent(viewModel)
        }
    }
}

// 日历头部组件（应用大小缩放）
@Composable
fun CalendarHeader(
    viewModel: TaskViewModel,
    calendarSize: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    var view by remember { mutableStateOf(xjj.derrew.xzgn.ui.screens.CalendarView.MONTH) }
    val currentDate by viewModel.currentDate.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val holidays by viewModel.holidays.collectAsState()
    var selectedDate by remember { mutableStateOf<java.util.Date?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) }  // ← 添加对话框状态

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding((16 * calendarSize).dp),
        verticalArrangement = Arrangement.spacedBy((16 * calendarSize).dp)
    ) {
        // 日历标题
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shadowElevation = 4.dp,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(
                bottomStart = 20.dp,
                bottomEnd = 20.dp
            )
        ) {
            xjj.derrew.xzgn.ui.screens.CalendarHeader(
                currentDate = currentDate,
                onPreviousMonth = { viewModel.navigateToPreviousMonth() },
                onNextMonth = { viewModel.navigateToNextMonth() },
                onToday = { viewModel.navigateToToday() },
                calendarSize = calendarSize
            )
        }

        // 视图切换
        xjj.derrew.xzgn.ui.screens.ViewToggle(
            viewType = view,
            calendarSize = calendarSize,
            onViewTypeChange = { view = it }
        )

        // 星期标题
        xjj.derrew.xzgn.ui.screens.WeekHeaders(calendarSize = calendarSize)

        // 日历网格 - 月视图
        if (view == xjj.derrew.xzgn.ui.screens.CalendarView.MONTH) {
            RenderMonthView(currentDate, tasks, holidays, calendarSize) { date -> 
                selectedDate = date
                showTaskDialog = true  // ← 点击日期时显示对话框
            }
        } else {
            RenderWeekView(currentDate, tasks, holidays, calendarSize) { date -> 
                selectedDate = date
                showTaskDialog = true  // ← 点击日期时显示对话框
            }
        }

        // 热力图
        xjj.derrew.xzgn.ui.screens.TaskDensityLegend(calendarSize = calendarSize)
    }
    
    // ✅ 任务对话框
    if (showTaskDialog && selectedDate != null) {
        val tasksForSelectedDate = tasks.filter { 
            xjj.derrew.xzgn.ui.screens.isSameDay(it.dueDate, selectedDate!!) 
        }.sortedBy { it.order }
        
        xjj.derrew.xzgn.ui.screens.TaskDialog(
            date = selectedDate!!,
            tasks = tasksForSelectedDate,
            onDismiss = {
                showTaskDialog = false
                selectedDate = null
            },
            onTaskUpdate = { task -> viewModel.updateTask(task) },
            onTaskDelete = { task -> viewModel.deleteTask(task) },
            onTaskReorder = { tasks -> viewModel.updateTaskOrder(tasks) },
            onTaskAdd = { task -> viewModel.addTask(task) }
        )
    }
}

// 渲染月视图
@Composable
fun RenderMonthView(
    currentDate: java.util.Date,
    tasks: List<Task>,
    holidays: List<Holiday>,
    calendarSize: Float = 1.0f,
    onDateClick: (java.util.Date) -> Unit
) {
    // 缓存日期计算结果，避免每次重组都重新计算
    val days = remember(currentDate) {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = currentDate
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))

        val daysList = mutableListOf<java.util.Date>()
        repeat(42) {
            daysList.add(calendar.time)
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        daysList
    }

    Column {
        for (week in 0 until 6) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                for (day in 0 until 7) {
                    val dayIndex = week * 7 + day
                    val date = days[dayIndex]
                    val tasksForDay = tasks.filter { xjj.derrew.xzgn.ui.screens.isSameDay(it.dueDate, date) }
                    val totalTasks = tasksForDay.size
                    val completedTasks = tasksForDay.count { it.isCompleted }
                    val isCurrentMonth = date.month == currentDate.month
                    val isToday = xjj.derrew.xzgn.ui.screens.isSameDay(date, java.util.Date())
                    val isPast = xjj.derrew.xzgn.ui.screens.isPastDate(date)
                    val isHoliday = holidays.any { xjj.derrew.xzgn.ui.screens.isSameDay(it.date, date) }

                    Box(modifier = Modifier.weight(1f)) {
                        xjj.derrew.xzgn.ui.screens.CalendarDay(
                            date = date,
                            isCurrentMonth = isCurrentMonth,
                            isToday = isToday,
                            isPast = isPast,
                            totalTasks = totalTasks,
                            completedTasks = completedTasks,
                            isHoliday = isHoliday,
                            tasks = tasksForDay,
                            calendarSize = calendarSize,
                            onClick = { onDateClick(date) }
                        )
                    }
                }
            }
        }
    }
}

// 渲染周视图
@Composable
fun RenderWeekView(
    currentDate: java.util.Date,
    tasks: List<Task>,
    holidays: List<Holiday>,
    calendarSize: Float = 1.0f,
    onDateClick: (java.util.Date) -> Unit
) {
    // 缓存周视图日期计算结果
    val days = remember(currentDate) {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = currentDate
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -(dayOfWeek - 1))

        val daysList = mutableListOf<java.util.Date>()
        repeat(7) {
            daysList.add(calendar.time)
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        daysList
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        days.forEach { date ->
            val tasksForDay = tasks.filter { xjj.derrew.xzgn.ui.screens.isSameDay(it.dueDate, date) }
            val totalTasks = tasksForDay.size
            val completedTasks = tasksForDay.count { it.isCompleted }
            val isCurrentMonth = true
            val isToday = xjj.derrew.xzgn.ui.screens.isSameDay(date, java.util.Date())
            val isPast = xjj.derrew.xzgn.ui.screens.isPastDate(date)
            val isHoliday = holidays.any { xjj.derrew.xzgn.ui.screens.isSameDay(it.date, date) }

            Box(modifier = Modifier.weight(1f)) {
                xjj.derrew.xzgn.ui.screens.CalendarDay(
                    date = date,
                    isCurrentMonth = isCurrentMonth,
                    isToday = isToday,
                    isPast = isPast,
                    totalTasks = totalTasks,
                    completedTasks = completedTasks,
                    isHoliday = isHoliday,
                    tasks = tasksForDay,
                    calendarSize = calendarSize,
                    onClick = { onDateClick(date) }
                )
            }
        }
    }
}

// 日历下方任务列表内容（不再需要LazyColumn，因为已在外层）
@Composable
fun CalendarTaskListContent(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val context = LocalContext.current
    
    // 状态：选中的任务和对话框显示
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var showTaskDetailDialog by remember { mutableStateOf(false) }

    // 优化：缓存月份任务过滤和排序结果，避免每次重组都重新计算
    val monthTasks = remember(tasks, currentDate) {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = currentDate
        val currentMonth = calendar.get(java.util.Calendar.MONTH)
        val currentYear = calendar.get(java.util.Calendar.YEAR)

        tasks.filter { task ->
            val taskCalendar = java.util.Calendar.getInstance()
            taskCalendar.time = task.dueDate
            taskCalendar.get(java.util.Calendar.MONTH) == currentMonth &&
            taskCalendar.get(java.util.Calendar.YEAR) == currentYear
        }.sortedBy { it.dueDate }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = context.getString(R.string.monthly_tasks),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (monthTasks.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = context.getString(R.string.no_tasks_this_month),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            monthTasks.forEach { task ->
                xjj.derrew.xzgn.ui.screens.TaskItem(
                    task = task,
                    onTaskUpdate = { updatedTask ->
                        viewModel.updateTask(updatedTask)
                    },
                    onTaskDelete = { deletedTask ->
                        viewModel.deleteTask(deletedTask)
                    },
                    onTaskClick = { taskItem ->
                        selectedTask = taskItem
                        showTaskDetailDialog = true
                    }
                )
            }
        }
    }
    
    // 任务详情对话框
    if (showTaskDetailDialog && selectedTask != null) {
        xjj.derrew.xzgn.ui.screens.TaskDetailDialog(
            task = selectedTask!!,
            onDismiss = {
                showTaskDetailDialog = false
                selectedTask = null
            },
            onEdit = { task -> 
                viewModel.updateTask(task)
            },
            onDelete = { task -> 
                viewModel.deleteTask(task)
            },
            onToggleComplete = { task ->
                viewModel.updateTask(task)
            }
        )
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "calendar",
        modifier = modifier
    ) {
        composable("calendar") { 
            CalendarScreen(viewModel) 
        }
        composable("tasks") { 
            TaskListScreen(viewModel) 
        }
        composable("statistics") { 
            StatisticsScreen(viewModel) 
        }
        composable("settings") {
            SettingsScreen(
                onNavigateToThemeSettings = {
                    navController.navigate("theme_settings")
                },
                onNavigateToLanguageSettings = {
                    navController.navigate("language_settings")
                }
            )
        }
        composable("theme_settings") {
            ThemeSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("language_settings") {
            LanguageSettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
