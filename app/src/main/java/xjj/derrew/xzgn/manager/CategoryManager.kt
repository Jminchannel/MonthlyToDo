package xjj.derrew.xzgn.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import xjj.derrew.xzgn.R

// DataStore extension
private val Context.categoryDataStore: DataStore<Preferences> by preferencesDataStore(name = "category_settings")

/**
 * CategoryManager - 管理用户自定义的任务分类
 * 使用 DataStore 进行持久化存储
 */
class CategoryManager(private val context: Context) {
    
    companion object {
        private val CATEGORIES_KEY = stringSetPreferencesKey("custom_categories")
        
        @Volatile
        private var INSTANCE: CategoryManager? = null
        
        fun getInstance(context: Context): CategoryManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CategoryManager(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        // 默认分类的 key（用于多语言）
        const val DEFAULT_GENERAL = "category_general"
        const val DEFAULT_WORK = "category_work"
        const val DEFAULT_PERSONAL = "category_personal"
        const val DEFAULT_HEALTH = "category_health"
        const val DEFAULT_EDUCATION = "category_education"
    }
    
    /**
     * 获取所有分类（默认分类 + 自定义分类）
     */
    fun getAllCategories(): Flow<List<String>> {
        return context.categoryDataStore.data.map { preferences ->
            val customCategories = preferences[CATEGORIES_KEY]?.toList() ?: emptyList()
            val defaultCategories = getDefaultCategories()
            
            // 默认分类在前，自定义分类在后
            defaultCategories + customCategories
        }
    }
    
    /**
     * 获取默认分类（本地化）
     */
    fun getDefaultCategories(): List<String> {
        return listOf(
            context.getString(R.string.category_general),
            context.getString(R.string.category_work),
            context.getString(R.string.category_personal),
            context.getString(R.string.category_health),
            context.getString(R.string.category_education)
        )
    }
    
    /**
     * 获取自定义分类
     */
    fun getCustomCategories(): Flow<List<String>> {
        return context.categoryDataStore.data.map { preferences ->
            preferences[CATEGORIES_KEY]?.toList() ?: emptyList()
        }
    }
    
    /**
     * 添加新分类
     */
    suspend fun addCategory(category: String): Result<Unit> {
        if (category.isBlank()) {
            return Result.failure(Exception("分类名称不能为空"))
        }
        
        return try {
            context.categoryDataStore.edit { preferences ->
                val currentCategories = preferences[CATEGORIES_KEY]?.toMutableSet() ?: mutableSetOf()
                val allCategories = getDefaultCategories() + currentCategories.toList()
                
                if (allCategories.contains(category)) {
                    throw Exception("分类已存在")
                }
                
                currentCategories.add(category)
                preferences[CATEGORIES_KEY] = currentCategories
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 删除分类
     */
    suspend fun deleteCategory(category: String): Result<Unit> {
        // 不允许删除默认分类
        if (getDefaultCategories().contains(category)) {
            return Result.failure(Exception("不能删除默认分类"))
        }
        
        return try {
            context.categoryDataStore.edit { preferences ->
                val currentCategories = preferences[CATEGORIES_KEY]?.toMutableSet() ?: mutableSetOf()
                currentCategories.remove(category)
                preferences[CATEGORIES_KEY] = currentCategories
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 重命名分类
     */
    suspend fun renameCategory(oldName: String, newName: String): Result<Unit> {
        if (newName.isBlank()) {
            return Result.failure(Exception("分类名称不能为空"))
        }
        
        // 不允许重命名默认分类
        if (getDefaultCategories().contains(oldName)) {
            return Result.failure(Exception("不能重命名默认分类"))
        }
        
        return try {
            context.categoryDataStore.edit { preferences ->
                val currentCategories = preferences[CATEGORIES_KEY]?.toMutableSet() ?: mutableSetOf()
                val allCategories = getDefaultCategories() + currentCategories.toList()
                
                if (allCategories.contains(newName) && oldName != newName) {
                    throw Exception("分类名称已存在")
                }
                
                currentCategories.remove(oldName)
                currentCategories.add(newName)
                preferences[CATEGORIES_KEY] = currentCategories
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * 检查分类是否存在
     */
    suspend fun isCategoryExists(category: String): Boolean {
        var exists = false
        context.categoryDataStore.data.collect { preferences ->
            val customCategories = preferences[CATEGORIES_KEY]?.toList() ?: emptyList()
            val allCategories = getDefaultCategories() + customCategories
            exists = allCategories.contains(category)
        }
        return exists
    }
    
    /**
     * 检查是否是默认分类
     */
    fun isDefaultCategory(category: String): Boolean {
        return getDefaultCategories().contains(category)
    }
}

