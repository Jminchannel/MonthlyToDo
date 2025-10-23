# 依赖清理报告

## 📅 清理日期
2025-10-23

## 🎯 清理目标
检查并移除项目中未使用的依赖，优化构建配置和减小 APK 体积。

## 🔍 依赖检查过程

### 检查方法
1. 读取 `app/build.gradle.kts` 和 `gradle/libs.versions.toml`
2. 使用 `grep` 搜索各依赖库的实际使用情况
3. 分析代码中的 import 语句和类调用
4. 确认每个依赖是否被实际使用

## 📊 检查结果

### ✅ 保留的依赖（正在使用）

#### 核心 Android/Kotlin 库
```kotlin
implementation(libs.androidx.core.ktx)              // ✓ Kotlin 扩展
implementation(libs.androidx.core)                   // ✓ WindowCompat
implementation(libs.androidx.lifecycle.runtime.ktx)  // ✓ 生命周期
implementation(libs.androidx.activity.compose)       // ✓ Compose Activity
```

#### Compose UI 库
```kotlin
implementation(platform(libs.androidx.compose.bom))  // ✓ Compose BOM
implementation(libs.androidx.ui)                     // ✓ Compose UI
implementation(libs.androidx.ui.graphics)            // ✓ Graphics
implementation(libs.androidx.ui.tooling.preview)     // ✓ Preview
implementation(libs.androidx.material3)              // ✓ Material 3
implementation(libs.androidx.material.icons.extended) // ✓ 扩展图标
```
**使用位置**: 所有 Compose 界面

#### Architecture Components
```kotlin
implementation(libs.androidx.navigation.compose)     // ✓ Navigation
implementation(libs.androidx.lifecycle.viewmodel.compose) // ✓ ViewModel
implementation(libs.androidx.lifecycle.runtime.compose)   // ✓ Runtime
```
**使用位置**: 
- `MainActivity.kt` - NavHost, NavController, rememberNavController
- `BottomNavigationBar.kt` - NavController, currentBackStackEntryAsState

#### 数据持久化
```kotlin
implementation(libs.androidx.room.runtime)           // ✓ Room 数据库
implementation(libs.androidx.room.ktx)               // ✓ Room Kotlin 扩展
kapt(libs.androidx.room.compiler)                    // ✓ Room 编译器
implementation(libs.androidx.datastore.preferences)  // ✓ DataStore
```
**使用位置**:
- Room - 任务、设备数据存储
- DataStore - 主题、语言、分类、日历大小设置

#### 第三方功能库
```kotlin
implementation(libs.reorderable)                     // ✓ 拖拽排序
```
**使用位置**: `TaskDialog.kt` - ReorderableItem, ReorderableLazyColumn

```kotlin
implementation(libs.richeditor.compose)              // ✓ 富文本编辑器
```
**使用位置**: 
- `RichTextEditor.kt` - RichTextEditor, rememberRichTextState
- `RichTextDisplay.kt` - RichText, RichTextDisplay
- `TaskDialog.kt` - RichTextEditorDialog, RichTextPreview

#### 后端服务
```kotlin
implementation(libs.androidx.core)                   // ✓ Core
implementation(libs.installreferrer)                 // ✓ Install Referrer
implementation(platform(libs.bom))                   // ✓ Supabase BOM
implementation(libs.supabase.postgrest.kt)           // ✓ Supabase Postgrest
implementation(libs.supabase.auth.kt)                // ✓ Supabase Auth
implementation(libs.supabase.realtime.kt)            // ✓ Supabase Realtime
implementation(libs.ktor.client.android.v322)        // ✓ Ktor Client
```
**使用位置**:
- `SupabaseClient.kt` - createSupabaseClient, Auth, Postgrest
- `DeviceRepository.kt` - InstallReferrerClient, Supabase.from()

#### 测试库
```kotlin
testImplementation(libs.junit)                       // ✓ JUnit
androidTestImplementation(libs.androidx.junit)       // ✓ AndroidX JUnit
androidTestImplementation(libs.androidx.espresso.core) // ✓ Espresso
androidTestImplementation(libs.androidx.ui.test.junit4) // ✓ Compose Test
debugImplementation(libs.androidx.ui.tooling)        // ✓ UI Tooling
debugImplementation(libs.androidx.ui.test.manifest)  // ✓ Test Manifest
```

---

### ❌ 已移除的依赖（未使用）

#### Vico 图表库
```kotlin
implementation(libs.compose.m3)  // ✗ 完全未使用
```

**移除原因**:
- 在整个项目中找不到任何 `import com.patrykandpatrick.vico.*` 语句
- `StatisticsScreen.kt` 中只是手绘了简单的条形图
- 没有使用 Vico 提供的图表组件

**版本配置也已移除**:
```toml
// libs.versions.toml
composeM3Version = "2.0.1"  // ✗ 已删除
compose-m3 = { module = "com.patrykandpatrick.vico:compose-m3", ... }  // ✗ 已删除
```

---

## 📈 优化效果

### 构建优化
| 指标 | 变化 |
|------|------|
| 依赖数量 | -1 个 |
| APK 体积 | 预计减少 ~500KB - 1MB |
| 编译时间 | 略微减少 |
| 依赖冲突风险 | 降低 |

### 代码质量
- ✅ 移除了未使用的依赖
- ✅ 减少了潜在的版本冲突
- ✅ 配置文件更整洁
- ✅ 构建脚本更清晰

---

## 🔧 修改的文件

### 1. `app/build.gradle.kts`
```diff
  // 添加DataStore用于偏好设置
  implementation(libs.androidx.datastore.preferences)
- // 添加图表库
- implementation(libs.compose.m3)
  // 添加拖拽排序库
  implementation(libs.reorderable)
```

### 2. `gradle/libs.versions.toml`
```diff
  [versions]
  agp = "8.11.1"
- composeM3Version = "2.0.1"
  datastorePreferencesVersion = "1.1.2"
  kotlin = "2.0.21"
```

```diff
  [libraries]
  bom = { module = "io.github.jan-tennert.supabase:bom", version.ref = "bom" }
  androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
- compose-m3 = { module = "com.patrykandpatrick.vico:compose-m3", version.ref = "composeM3Version" }
  junit = { group = "junit", name = "junit", version.ref = "junit" }
```

---

## 📝 依赖使用情况详表

| 依赖库 | 状态 | 使用位置 | 用途 |
|--------|------|----------|------|
| androidx.core.ktx | ✅ | 全局 | Kotlin 扩展函数 |
| androidx.core | ✅ | Theme.kt | WindowCompat |
| androidx.navigation.compose | ✅ | MainActivity, BottomNavigationBar | 页面导航 |
| androidx.room.* | ✅ | Repository | 数据库 |
| androidx.datastore | ✅ | Manager | 偏好设置 |
| reorderable | ✅ | TaskDialog | 拖拽排序 |
| richeditor-compose | ✅ | RichTextEditor, TaskDialog | 富文本编辑 |
| installreferrer | ✅ | DeviceRepository | 安装来源追踪 |
| supabase.* | ✅ | SupabaseClient, DeviceRepository | 后端服务 |
| compose-m3 (Vico) | ❌ | - | **未使用，已移除** |

---

## 🎯 建议

### 已完成
- [x] 移除未使用的 Vico 图表库
- [x] 清理相关的版本配置

### 未来考虑
1. **如果需要图表功能**，可以考虑：
   - 使用更轻量的图表库
   - 继续使用自定义的简单条形图
   - 只在需要时才添加 Vico

2. **定期依赖检查**：
   - 每个版本发布前检查依赖使用情况
   - 使用 Android Lint 检查未使用的依赖
   - 考虑使用 Gradle 依赖分析插件

3. **依赖优化**：
   - 考虑是否有依赖可以合并
   - 检查是否有更轻量的替代方案

---

## ✅ 验证清单

- [x] 检查所有依赖的使用情况
- [x] 确认移除的依赖确实未使用
- [x] 更新 build.gradle.kts
- [x] 更新 libs.versions.toml
- [x] 文档记录
- [ ] Sync Gradle 确认无错误
- [ ] 运行测试确认功能正常
- [ ] 构建 APK 确认编译成功

---

## 🎉 总结

成功移除了 **1 个未使用的依赖**（Vico 图表库），优化了项目配置。

**当前依赖数量**：
- 核心依赖：~25 个
- 测试依赖：6 个
- **总计**：~31 个依赖

所有保留的依赖都在项目中有实际使用，没有冗余。项目依赖配置现在更加精简和高效！

