package xjj.derrew.xzgn.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore extension
private val Context.calendarSettingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "calendar_settings")

/**
 * CalendarSettingsManager - 管理日历相关设置
 */
class CalendarSettingsManager(private val context: Context) {
    
    companion object {
        private val CALENDAR_SIZE_KEY = floatPreferencesKey("calendar_size")
        
        // 日历大小选项
        const val SIZE_SMALL = 0.8f
        const val SIZE_MEDIUM = 1.0f
        const val SIZE_LARGE = 1.2f
        const val SIZE_EXTRA_LARGE = 1.4f
        
        @Volatile
        private var INSTANCE: CalendarSettingsManager? = null
        
        fun getInstance(context: Context): CalendarSettingsManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CalendarSettingsManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    /**
     * 获取日历大小
     */
    fun getCalendarSize(): Flow<Float> {
        return context.calendarSettingsDataStore.data.map { preferences ->
            preferences[CALENDAR_SIZE_KEY] ?: SIZE_MEDIUM
        }
    }
    
    /**
     * 设置日历大小
     */
    suspend fun setCalendarSize(size: Float) {
        context.calendarSettingsDataStore.edit { preferences ->
            preferences[CALENDAR_SIZE_KEY] = size
        }
    }
}

