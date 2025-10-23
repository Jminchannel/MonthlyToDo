package xjj.derrew.xzgn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import xjj.derrew.xzgn.R
import xjj.derrew.xzgn.manager.CategoryManager
import xjj.derrew.xzgn.viewmodel.TaskViewModel

/**
 * 分类管理页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(
    taskViewModel: TaskViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val categoryManager = remember { CategoryManager.getInstance(context) }
    val scope = rememberCoroutineScope()
    
    // 状态
    val allCategories by categoryManager.getAllCategories().collectAsState(initial = emptyList())
    val customCategories by categoryManager.getCustomCategories().collectAsState(initial = emptyList())
    val tasks by taskViewModel.tasks.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCannotDeleteDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    var categoryTaskCount by remember { mutableStateOf(0) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    
    val snackbarHostState = remember { SnackbarHostState() }

    // 显示 Snackbar
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            snackbarMessage = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.category_management)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text(stringResource(R.string.add_category)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 提示信息
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.category_management_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 分类列表
            if (allCategories.isEmpty()) {
                // 空状态
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.no_categories),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allCategories) { category ->
                        val isDefault = categoryManager.isDefaultCategory(category)
                        CategoryItem(
                            category = category,
                            isDefault = isDefault,
                            onEdit = {
                                selectedCategory = category
                                showEditDialog = true
                            },
                            onDelete = {
                                selectedCategory = category
                                // 检查该分类是否被使用
                                val count = tasks.count { it.category == category }
                                if (count > 0) {
                                    // 有任务使用该分类，显示无法删除对话框
                                    categoryTaskCount = count
                                    showCannotDeleteDialog = true
                                } else {
                                    // 没有任务使用，可以删除
                                    showDeleteDialog = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // 添加分类对话框
    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { newCategory ->
                scope.launch {
                    val result = categoryManager.addCategory(newCategory)
                    result.onSuccess {
                        snackbarMessage = context.getString(R.string.category_added_success)
                        showAddDialog = false
                    }.onFailure { error ->
                        snackbarMessage = error.message ?: context.getString(R.string.category_add_failed)
                    }
                }
            }
        )
    }

    // 编辑分类对话框
    if (showEditDialog) {
        EditCategoryDialog(
            currentName = selectedCategory,
            onDismiss = { showEditDialog = false },
            onConfirm = { newName ->
                scope.launch {
                    val result = categoryManager.renameCategory(selectedCategory, newName)
                    result.onSuccess {
                        snackbarMessage = context.getString(R.string.category_renamed_success)
                        showEditDialog = false
                    }.onFailure { error ->
                        snackbarMessage = error.message ?: context.getString(R.string.category_rename_failed)
                    }
                }
            }
        )
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            title = { Text(stringResource(R.string.delete_category_title)) },
            text = { Text(stringResource(R.string.delete_category_message, selectedCategory)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            val result = categoryManager.deleteCategory(selectedCategory)
                            result.onSuccess {
                                snackbarMessage = context.getString(R.string.category_deleted_success)
                                showDeleteDialog = false
                            }.onFailure { error ->
                                snackbarMessage = error.message ?: context.getString(R.string.category_delete_failed)
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    
    // 无法删除对话框（分类被使用时）
    if (showCannotDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showCannotDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text(stringResource(R.string.cannot_delete_category_title)) },
            text = { 
                Text(stringResource(R.string.cannot_delete_category_message, selectedCategory, categoryTaskCount))
            },
            confirmButton = {
                TextButton(onClick = { showCannotDeleteDialog = false }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}

/**
 * 分类列表项
 */
@Composable
fun CategoryItem(
    category: String,
    isDefault: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (isDefault) {
                        Text(
                            text = stringResource(R.string.default_category),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!isDefault) {
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = stringResource(R.string.default_category),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * 添加分类对话框
 */
@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Add, contentDescription = null) },
        title = { Text(stringResource(R.string.add_category)) },
        text = {
            Column {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = {
                        categoryName = it
                        errorMessage = null
                    },
                    label = { Text(stringResource(R.string.category_name)) },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        categoryName.isBlank() -> errorMessage = "分类名称不能为空"
                        else -> onConfirm(categoryName.trim())
                    }
                }
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

/**
 * 编辑分类对话框
 */
@Composable
fun EditCategoryDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var categoryName by remember { mutableStateOf(currentName) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Edit, contentDescription = null) },
        title = { Text(stringResource(R.string.edit_category)) },
        text = {
            Column {
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = {
                        categoryName = it
                        errorMessage = null
                    },
                    label = { Text(stringResource(R.string.category_name)) },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    when {
                        categoryName.isBlank() -> errorMessage = "分类名称不能为空"
                        else -> onConfirm(categoryName.trim())
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

