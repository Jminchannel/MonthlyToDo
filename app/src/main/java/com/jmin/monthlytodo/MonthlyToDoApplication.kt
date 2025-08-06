package com.jmin.monthlytodo

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.jmin.monthlytodo.manager.LanguageManager
import java.util.*

class MonthlyToDoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize language manager
        LanguageManager.initialize(this)
        LanguageManager.applyLanguage(this, LanguageManager.getCurrentLanguage())
    }
    
    override fun attachBaseContext(base: Context) {
        // Initialize language manager early
        LanguageManager.initialize(base)
        val language = LanguageManager.getCurrentLanguage()
        val locale = when (language) {
            com.jmin.monthlytodo.manager.Language.ENGLISH -> Locale.ENGLISH
            com.jmin.monthlytodo.manager.Language.CHINESE_SIMPLIFIED -> Locale.SIMPLIFIED_CHINESE
            com.jmin.monthlytodo.manager.Language.CHINESE_TRADITIONAL -> Locale.TRADITIONAL_CHINESE
            com.jmin.monthlytodo.manager.Language.JAPANESE -> Locale.JAPANESE
            com.jmin.monthlytodo.manager.Language.INDONESIAN -> Locale("in", "ID")
        }
        
        val configuration = Configuration(base.resources.configuration)
        configuration.setLocale(locale)
        val context = base.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }
}
