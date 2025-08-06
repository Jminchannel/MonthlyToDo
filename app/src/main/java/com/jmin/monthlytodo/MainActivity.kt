package com.jmin.monthlytodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jmin.monthlytodo.database.AppDatabase
import com.jmin.monthlytodo.repository.TaskRepository
import com.jmin.monthlytodo.ui.screens.AchievementsScreen
import com.jmin.monthlytodo.ui.screens.BottomNavigationBar
import com.jmin.monthlytodo.ui.screens.CalendarScreen
import com.jmin.monthlytodo.ui.screens.SettingsScreen
import com.jmin.monthlytodo.ui.screens.StatisticsScreen
import com.jmin.monthlytodo.ui.screens.TaskListScreen
import com.jmin.monthlytodo.ui.screens.ThemeSettingsScreen
import com.jmin.monthlytodo.ui.screens.LanguageSettingsScreen
import com.jmin.monthlytodo.ui.theme.MonthlyToDoTheme
import com.jmin.monthlytodo.viewmodel.TaskViewModel
import com.jmin.monthlytodo.manager.LanguageManager
import com.jmin.monthlytodo.viewmodel.DeviceViewModel

class MainActivity : ComponentActivity() {
    private val deviceViewModel: DeviceViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize language manager
        LanguageManager.initialize(this)
        LanguageManager.applyLanguage(this, LanguageManager.getCurrentLanguage())

        enableEdgeToEdge()
        
        // Initialize database and repository here, outside of any Composable functions
        val database = AppDatabase.getDatabase(this)
        val repository = TaskRepository(database.taskDao(), database.holidayDao())
        val viewModelFactory = TaskViewModelFactory(repository)
        val viewModel: TaskViewModel = ViewModelProvider(this, viewModelFactory)[TaskViewModel::class.java]
        
        setContent {
            MonthlyToDoTheme {
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
    
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
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
        composable("achievements") {
            AchievementsScreen(viewModel)
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
