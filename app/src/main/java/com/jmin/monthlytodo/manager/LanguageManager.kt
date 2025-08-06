package com.jmin.monthlytodo.manager

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

object LanguageManager {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"
    
    private val _currentLanguage = MutableStateFlow(Language.ENGLISH)
    val currentLanguage: StateFlow<Language> = _currentLanguage
    
    private lateinit var sharedPreferences: SharedPreferences
    
    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedLanguage = sharedPreferences.getString(KEY_LANGUAGE, Language.ENGLISH.code)
        _currentLanguage.value = Language.fromCode(savedLanguage ?: Language.ENGLISH.code)
    }
    
    fun setLanguage(context: Context, language: Language) {
        _currentLanguage.value = language
        sharedPreferences.edit().putString(KEY_LANGUAGE, language.code).apply()
        applyLanguage(context, language)
    }
    
    fun applyLanguage(context: Context, language: Language) {
        val locale = when (language) {
            Language.ENGLISH -> Locale.ENGLISH
            Language.CHINESE_SIMPLIFIED -> Locale.SIMPLIFIED_CHINESE
            Language.CHINESE_TRADITIONAL -> Locale.TRADITIONAL_CHINESE
            Language.JAPANESE -> Locale.JAPANESE
            Language.INDONESIAN -> Locale("in", "ID")
        }
        
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }
    
    fun getCurrentLanguage(): Language {
        return _currentLanguage.value
    }
}

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    CHINESE_SIMPLIFIED("zh-CN", "Chinese Simplified", "简体中文"),
    CHINESE_TRADITIONAL("zh-TW", "Chinese Traditional", "繁體中文"),
    JAPANESE("ja", "Japanese", "日本語"),
    INDONESIAN("in", "Indonesian", "Bahasa Indonesia");
    
    companion object {
        fun fromCode(code: String): Language {
            return values().find { it.code == code } ?: ENGLISH
        }
    }
}
