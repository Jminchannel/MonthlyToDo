# 日历大小设置功能修复

## 🐛 问题描述

用户反馈：**日历大小调节没有生效**

## 🔍 问题分析

初始实现中，`calendarSize` 参数只应用到了 `CalendarHeader` 的外层 padding，但没有深入应用到各个子组件中，导致：
- ✗ 日历标题字体大小没有缩放
- ✗ 导航按钮大小没有缩放
- ✗ 星期标题字体大小没有缩放
- ✗ 日期单元格大小没有缩放
- ✗ 日期数字大小没有缩放
- ✗ 视图切换按钮没有缩放
- ✗ 热力图图例没有缩放

## ✅ 修复方案

### 1. 修改 `CalendarScreen.kt` - 所有日历组件支持 `calendarSize`

#### A. CalendarHeader 组件

```kotlin
@Composable
fun CalendarHeader(
    currentDate: Date,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit,
    calendarSize: Float = 1.0f,  // ← 新增参数
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding((12 * calendarSize).dp),  // ← 应用缩放
        ...
    ) {
        Text(
            text = dateFormat.format(currentDate),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = (24 * calendarSize).sp  // ← 字体大小缩放
            ),
            ...
        )
        
        // 导航按钮图标大小缩放
        Icon(
            ...,
            modifier = Modifier.size((24 * calendarSize).dp)
        )
    }
}
```

**应用缩放：**
- ✅ Padding: `12.dp` → `(12 * calendarSize).dp`
- ✅ 标题字体: `24.sp` → `(24 * calendarSize).sp`
- ✅ 图标大小: `24.dp` → `(24 * calendarSize).dp`

#### B. WeekHeaders 组件

```kotlin
@Composable
fun WeekHeaders(
    calendarSize: Float = 1.0f,  // ← 新增参数
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = (4 * calendarSize).dp),  // ← 应用缩放
        ...
    ) {
        daysOfWeek.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = (12 * calendarSize).sp  // ← 字体大小缩放
                ),
                ...
            )
        }
    }
}
```

**应用缩放：**
- ✅ Padding: `4.dp` → `(4 * calendarSize).dp`
- ✅ 字体大小: `12.sp` → `(12 * calendarSize).sp`

#### C. CalendarDay 组件

```kotlin
@Composable
fun CalendarDay(
    ...,
    calendarSize: Float = 1.0f,  // ← 新增参数
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding((2 * calendarSize).dp),  // ← 应用缩放
        ...
    ) {
        Box(
            modifier = Modifier
                .size((48 * calendarSize).dp)  // ← 单元格大小缩放
                ...
        ) {
            // 完成图标
            Icon(
                ...,
                modifier = Modifier.size((20 * calendarSize).dp)
            )
            
            // 进度指示器
            CircularProgressIndicator(
                ...,
                strokeWidth = (2 * calendarSize).dp
            )
            
            // 日期数字
            Text(
                ...,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = ((if (isDarkTheme) 14 else 12) * calendarSize).sp
                ),
                ...
            )
        }
    }
}
```

**应用缩放：**
- ✅ Padding: `2.dp` → `(2 * calendarSize).dp`
- ✅ 单元格大小: `48.dp` → `(48 * calendarSize).dp`
- ✅ 图标大小: `20.dp` → `(20 * calendarSize).dp`
- ✅ 进度条宽度: `2.dp` → `(2 * calendarSize).dp`
- ✅ 日期字体: `12-14.sp` → `(12-14 * calendarSize).sp`

#### D. ViewToggle 组件

```kotlin
@Composable
fun ViewToggle(
    viewType: CalendarView,
    calendarSize: Float = 1.0f,  // ← 新增参数
    onViewTypeChange: (CalendarView) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape((12 * calendarSize).dp)),  // ← 应用缩放
        ...
    ) {
        Button(
            ...,
            modifier = Modifier
                .weight(1f)
                .padding((4 * calendarSize).dp),  // ← 应用缩放
            shape = RoundedCornerShape((8 * calendarSize).dp)
        ) {
            Text(
                text = stringResource(R.string.month_view),
                fontSize = (14 * calendarSize).sp  // ← 字体大小缩放
            )
        }
    }
}
```

**应用缩放：**
- ✅ 容器圆角: `12.dp` → `(12 * calendarSize).dp`
- ✅ Button padding: `4.dp` → `(4 * calendarSize).dp`
- ✅ Button 圆角: `8.dp` → `(8 * calendarSize).dp`
- ✅ 文字大小: `14.sp` → `(14 * calendarSize).sp`

#### E. TaskDensityLegend 组件

```kotlin
@Composable
fun TaskDensityLegend(
    calendarSize: Float = 1.0f  // ← 新增参数
) {
    Column {
        Text(
            text = stringResource(R.string.task_density),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = (14 * calendarSize).sp  // ← 字体大小缩放
            ),
            ...
        )

        Spacer(modifier = Modifier.height((8 * calendarSize).dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy((4 * calendarSize).dp)  // ← 应用缩放
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy((4 * calendarSize).dp)
            ) {
                items(...) { (count, label) ->
                    Box(
                        modifier = Modifier
                            .size((15 * calendarSize).dp)  // ← 色块大小缩放
                            .clip(RoundedCornerShape((2 * calendarSize).dp))
                            ...
                    )
                    
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = (11 * calendarSize).sp  // ← 字体大小缩放
                        ),
                        ...
                    )
                }
            }
        }
    }
}
```

**应用缩放：**
- ✅ 标题字体: `14.sp` → `(14 * calendarSize).sp`
- ✅ Spacer 高度: `8.dp` → `(8 * calendarSize).dp`
- ✅ 间距: `4.dp` → `(4 * calendarSize).dp`
- ✅ 色块大小: `15.dp` → `(15 * calendarSize).dp`
- ✅ 圆角: `2.dp` → `(2 * calendarSize).dp`
- ✅ 标签字体: `11.sp` → `(11 * calendarSize).sp`

### 2. 修改 `MainActivity.kt` - 传递 `calendarSize` 参数

#### A. CalendarHeader 中的所有调用

```kotlin
@Composable
fun CalendarHeader(
    viewModel: TaskViewModel,
    calendarSize: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    Column(...) {
        // 传递 calendarSize 到 CalendarHeader
        xjj.derrew.xzgn.ui.screens.CalendarHeader(
            currentDate = currentDate,
            onPreviousMonth = { viewModel.navigateToPreviousMonth() },
            onNextMonth = { viewModel.navigateToNextMonth() },
            onToday = { viewModel.navigateToToday() },
            calendarSize = calendarSize  // ← 传递参数
        )

        // 传递 calendarSize 到 ViewToggle
        xjj.derrew.xzgn.ui.screens.ViewToggle(
            viewType = view,
            calendarSize = calendarSize,  // ← 传递参数
            onViewTypeChange = { view = it }
        )

        // 传递 calendarSize 到 WeekHeaders
        xjj.derrew.xzgn.ui.screens.WeekHeaders(
            calendarSize = calendarSize  // ← 传递参数
        )

        // 传递 calendarSize 到 RenderMonthView/RenderWeekView
        if (view == CalendarView.MONTH) {
            RenderMonthView(currentDate, tasks, holidays, calendarSize) { ... }
        } else {
            RenderWeekView(currentDate, tasks, holidays, calendarSize) { ... }
        }

        // 传递 calendarSize 到 TaskDensityLegend
        xjj.derrew.xzgn.ui.screens.TaskDensityLegend(
            calendarSize = calendarSize  // ← 传递参数
        )
    }
}
```

#### B. RenderMonthView 和 RenderWeekView

```kotlin
@Composable
fun RenderMonthView(
    currentDate: java.util.Date,
    tasks: List<Task>,
    holidays: List<Holiday>,
    calendarSize: Float = 1.0f,  // ← 新增参数
    onDateClick: (java.util.Date) -> Unit
) {
    // ... 计算日期 ...
    
    // 调用 CalendarDay 时传递 calendarSize
    xjj.derrew.xzgn.ui.screens.CalendarDay(
        date = date,
        ...,
        calendarSize = calendarSize,  // ← 传递参数
        onClick = { onDateClick(date) }
    )
}

@Composable
fun RenderWeekView(
    currentDate: java.util.Date,
    tasks: List<Task>,
    holidays: List<Holiday>,
    calendarSize: Float = 1.0f,  // ← 新增参数
    onDateClick: (java.util.Date) -> Unit
) {
    // ... 计算日期 ...
    
    // 调用 CalendarDay 时传递 calendarSize
    xjj.derrew.xzgn.ui.screens.CalendarDay(
        date = date,
        ...,
        calendarSize = calendarSize,  // ← 传递参数
        onClick = { onDateClick(date) }
    )
}
```

## 📊 修复对比

### 修复前
```
用户调节日历大小
  ↓
设置保存到 DataStore ✓
  ↓
CalendarWithTaskList 获取 calendarSize ✓
  ↓
传递到 CalendarHeader ✓
  ↓
只应用到外层 padding ✗
  ↓
子组件没有应用缩放 ✗
  ↓
视觉效果：没有明显变化 ✗
```

### 修复后
```
用户调节日历大小
  ↓
设置保存到 DataStore ✓
  ↓
CalendarWithTaskList 获取 calendarSize ✓
  ↓
传递到 CalendarHeader ✓
  ↓
应用到所有子组件 ✓
  ├─ CalendarHeader (标题、按钮) ✓
  ├─ ViewToggle (切换按钮) ✓
  ├─ WeekHeaders (星期标题) ✓
  ├─ CalendarDay (日期单元格) ✓
  └─ TaskDensityLegend (热力图) ✓
  ↓
视觉效果：整体等比缩放 ✓
```

## 🎯 修复后效果

### 小尺寸 (0.8x)
```
┌─────────────────────────┐
│  October 2025    < >    │  ← 较小的标题
│  [月视图] [周视图]       │  ← 较小的按钮
│  日 一 二 三 四 五 六    │  ← 较小的星期标题
│  ● ● ● ● ● ● ●         │  ← 较小的日期单元格
│  ■ 0  ■ 1-2  ■ 3-4     │  ← 较小的热力图
└─────────────────────────┘
紧凑视图，显示更多内容
```

### 中等尺寸 (1.0x - 默认)
```
┌──────────────────────────┐
│   October 2025    < >    │
│   [月视图] [周视图]       │
│   日 一 二 三 四 五 六    │
│   ● ● ● ● ● ● ●         │
│   ■ 0  ■ 1-2  ■ 3-4     │
└──────────────────────────┘
标准大小（推荐）
```

### 大尺寸 (1.2x)
```
┌────────────────────────────┐
│    October 2025    < >     │  ← 较大的标题
│    [月视图] [周视图]        │  ← 较大的按钮
│    日 一 二 三 四 五 六     │  ← 较大的星期标题
│    ●  ●  ●  ●  ●  ●  ●    │  ← 较大的日期单元格
│    ■ 0   ■ 1-2   ■ 3-4    │  ← 较大的热力图
└────────────────────────────┘
更大的日历，阅读更清晰
```

### 特大尺寸 (1.4x)
```
┌──────────────────────────────┐
│     October 2025    < >      │  ← 最大的标题
│     [月视图] [周视图]         │  ← 最大的按钮
│     日 一 二 三 四 五 六      │  ← 最大的星期标题
│     ●   ●   ●   ●   ●   ●  │  ← 最大的日期单元格
│     ■ 0    ■ 1-2    ■ 3-4   │  ← 最大的热力图
└──────────────────────────────┘
最大尺寸，最佳可见性
```

## ✅ 验证清单

- [x] 日历标题字体大小随 calendarSize 缩放
- [x] 导航按钮图标大小随 calendarSize 缩放
- [x] 视图切换按钮大小和字体随 calendarSize 缩放
- [x] 星期标题字体大小随 calendarSize 缩放
- [x] 日期单元格大小随 calendarSize 缩放
- [x] 日期数字字体大小随 calendarSize 缩放
- [x] 完成图标大小随 calendarSize 缩放
- [x] 进度条宽度随 calendarSize 缩放
- [x] 热力图标题字体大小随 calendarSize 缩放
- [x] 热力图色块大小随 calendarSize 缩放
- [x] 热力图标签字体大小随 calendarSize 缩放
- [x] 所有间距和 padding 随 calendarSize 缩放
- [x] 无编译错误

## 📝 测试建议

1. **功能测试**：
   ```
   1. 打开应用 → 设置 → 外观设置 → 日历大小
   2. 选择"小"，返回日历页面
   3. 观察：日历整体缩小，文字和图标也变小 ✓
   4. 选择"特大"，返回日历页面
   5. 观察：日历整体放大，文字和图标也变大 ✓
   ```

2. **视觉检查**：
   - [ ] 标题文字大小变化明显
   - [ ] 日期数字大小变化明显
   - [ ] 按钮大小变化明显
   - [ ] 整体视觉比例协调

3. **边界测试**：
   - [ ] 特大尺寸下，日历不会超出屏幕
   - [ ] 小尺寸下，文字仍然清晰可读
   - [ ] 快速切换不同尺寸，无异常

## 📊 代码统计

| 项目 | 修改 |
|-----|------|
| 修改的组件 | 6 个 (CalendarHeader, WeekHeaders, CalendarDay, ViewToggle, TaskDensityLegend, RenderMonthView/Week) |
| 修改的代码行数 | ~50 行 |
| 新增参数 | 6 个 `calendarSize` 参数 |
| 应用缩放的属性 | ~25 个 (padding, fontSize, size 等) |

## 🎉 总结

通过将 `calendarSize` 参数深入应用到所有日历子组件的尺寸相关属性中，成功实现了日历的整体等比缩放效果。

**修复要点**：
1. ✅ 所有组件都添加了 `calendarSize` 参数
2. ✅ 所有尺寸相关的属性都乘以 `calendarSize`
3. ✅ 参数从顶层正确传递到所有子组件
4. ✅ 保持了视觉比例的协调性

用户现在可以根据自己的偏好和屏幕大小，自由调节日历的显示大小了！

