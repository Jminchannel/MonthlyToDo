# 分类删除保护功能

## 📅 实现日期
2025-10-22

## 🎯 功能概述

添加了分类删除保护机制，当用户尝试删除一个正在被任务使用的分类时，系统会：
1. ✅ 检查该分类是否被任务使用
2. ✅ 如果被使用，阻止删除并显示提示对话框
3. ✅ 告知用户有多少个任务正在使用该分类
4. ✅ 提示用户需要先重新分配或删除这些任务

## 🔒 数据完整性保护

### 删除逻辑流程

```
用户点击删除分类
    ↓
检查该分类是否被任务使用
    ↓
    ├─ 有任务使用 → 显示"无法删除"对话框
    │                 ├─ 显示分类名称
    │                 ├─ 显示任务数量
    │                 └─ 提示用户先处理任务
    │
    └─ 无任务使用 → 显示"确认删除"对话框
                     └─ 用户确认后删除
```

## 📝 代码修改

### 1. CategoryManagementScreen.kt

#### 新增状态管理
```kotlin
// 新增状态
var showCannotDeleteDialog by remember { mutableStateOf(false) }
var categoryTaskCount by remember { mutableStateOf(0) }
val tasks by taskViewModel.tasks.collectAsState()
```

#### 修改删除逻辑
```kotlin
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
```

#### 新增"无法删除"对话框
```kotlin
if (showCannotDeleteDialog) {
    AlertDialog(
        onDismissRequest = { showCannotDeleteDialog = false },
        icon = { Icon(Icons.Default.Warning, tint = MaterialTheme.colorScheme.error) },
        title = { Text(stringResource(R.string.cannot_delete_category_title)) },
        text = { 
            Text(stringResource(
                R.string.cannot_delete_category_message, 
                selectedCategory, 
                categoryTaskCount
            ))
        },
        confirmButton = {
            TextButton(onClick = { showCannotDeleteDialog = false }) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
```

### 2. MainActivity.kt

#### 传递 TaskViewModel
```kotlin
CategoryManagementScreen(
    taskViewModel = viewModel,  // ← 新增参数
    onNavigateBack = {
        showCategoryManagement = false
    }
)
```

### 3. 多语言字符串资源

#### 英文 (values/strings.xml)
```xml
<string name="cannot_delete_category_title">Cannot Delete Category</string>
<string name="cannot_delete_category_message">
    The category \"%1$s\" cannot be deleted because it is currently being 
    used by %2$d task(s). Please reassign or delete these tasks first.
</string>
```

#### 简体中文 (values-zh-rCN/strings.xml)
```xml
<string name="cannot_delete_category_title">无法删除分类</string>
<string name="cannot_delete_category_message">
    分类「%1$s」无法删除，因为当前有 %2$d 个任务正在使用该分类。
    请先重新分配或删除这些任务。
</string>
```

#### 繁体中文 (values-zh-rTW/strings.xml)
```xml
<string name="cannot_delete_category_title">無法刪除分類</string>
<string name="cannot_delete_category_message">
    分類「%1$s」無法刪除，因為目前有 %2$d 個任務正在使用該分類。
    請先重新分配或刪除這些任務。
</string>
```

## 🎨 UI 展示

### 场景 1: 删除未使用的分类（正常流程）

```
┌─────────────────────────────┐
│ ⚠️  删除分类                 │
├─────────────────────────────┤
│ 确定要删除分类「运动」吗？    │
│                             │
│         取消      删除       │
└─────────────────────────────┘
```

### 场景 2: 删除正在使用的分类（被阻止）

```
┌─────────────────────────────┐
│ ⚠️  无法删除分类              │
├─────────────────────────────┤
│ 分类「运动」无法删除，因为    │
│ 当前有 3 个任务正在使用该分类。│
│ 请先重新分配或删除这些任务。  │
│                             │
│              确定            │
└─────────────────────────────┘
```

## 💡 用户体验

### 保护机制
1. **防止数据孤立**: 避免删除分类后任务失去分类
2. **明确提示**: 清楚告知用户无法删除的原因
3. **提供方案**: 指导用户如何处理（重新分配或删除任务）
4. **任务计数**: 显示具体有多少任务在使用

### 用户操作指引

#### 如果想删除正在使用的分类：

**方法 1: 重新分配任务**
1. 关闭对话框
2. 找到使用该分类的任务
3. 编辑这些任务，更改为其他分类
4. 返回分类管理页面重新删除

**方法 2: 删除任务**
1. 关闭对话框
2. 删除所有使用该分类的任务
3. 返回分类管理页面重新删除

## 🔍 技术实现

### 任务计数查询
```kotlin
// 高效的计数查询
val count = tasks.count { it.category == category }

// 等价于 SQL:
// SELECT COUNT(*) FROM tasks WHERE category = ?
```

### 实时数据同步
```kotlin
// 使用 Flow + collectAsState 确保数据实时性
val tasks by taskViewModel.tasks.collectAsState()

// 任何任务的添加/删除/修改都会自动触发重组
// 确保分类使用情况始终是最新的
```

### 对话框管理
```kotlin
// 使用独立的状态管理不同的对话框
var showDeleteDialog by remember { mutableStateOf(false) }        // 确认删除
var showCannotDeleteDialog by remember { mutableStateOf(false) }  // 无法删除
```

## ✅ 测试场景

### 功能测试
- [ ] 删除没有任务使用的分类 → 成功删除
- [ ] 删除有 1 个任务使用的分类 → 显示"无法删除"，提示 1 个任务
- [ ] 删除有多个任务使用的分类 → 显示"无法删除"，提示正确数量
- [ ] 删除分类前添加任务 → 检测到新任务，阻止删除
- [ ] 删除分类前移除任务 → 检测到任务已移除，允许删除

### UI 测试
- [ ] 对话框文字显示正确
- [ ] 分类名称正确显示
- [ ] 任务数量正确显示
- [ ] 图标颜色正确（红色警告）
- [ ] 按钮文字正确

### 多语言测试
- [ ] 英文提示正确
- [ ] 简体中文提示正确
- [ ] 繁体中文提示正确
- [ ] 格式化字符串参数正确

## 📊 优势对比

### 之前的行为
```
删除分类 → 直接删除
    ↓
❌ 任务失去分类信息
❌ 数据不一致
❌ 可能导致显示错误
```

### 现在的行为
```
删除分类 → 检查使用情况
    ↓
✅ 保护数据完整性
✅ 明确提示用户
✅ 避免误操作
✅ 提供解决方案
```

## 🚀 未来增强

### 短期改进
1. 在提示对话框中直接显示使用该分类的任务列表
2. 提供"查看相关任务"按钮，跳转到筛选后的任务列表
3. 提供"批量重新分配"功能，一键更改所有相关任务的分类

### 长期改进
1. 在删除前提供"替换分类"选项
   - 用户选择新分类
   - 自动将所有任务迁移到新分类
   - 然后删除旧分类
2. 分类使用统计
   - 在分类列表中显示每个分类的任务数量
   - 帮助用户更好地管理分类

## 📋 示例对话

### 用户操作流程示例

```
用户: "我想删除'运动'分类"
    ↓
[点击删除按钮]
    ↓
系统: "分类「运动」无法删除，因为当前有 3 个任务正在使用该分类。
      请先重新分配或删除这些任务。"
    ↓
用户: [点击确定] → 返回分类列表
    ↓
用户: [去任务列表找到相关任务]
    ↓
用户: [将3个任务改为其他分类或删除]
    ↓
用户: [返回分类管理]
    ↓
[再次点击删除按钮]
    ↓
系统: "确定要删除分类「运动」吗？"
    ↓
用户: [确认删除]
    ↓
系统: "分类删除成功" ✅
```

## 🎊 总结

成功实现了分类删除保护功能：

✅ **数据安全** - 防止删除正在使用的分类  
✅ **用户友好** - 清晰的提示和指引  
✅ **多语言** - 完整的本地化支持  
✅ **实时检查** - 基于最新的任务数据  
✅ **完整性** - 保护数据一致性  

这是一个重要的数据完整性保护机制，显著提升了应用的可靠性和用户体验。

