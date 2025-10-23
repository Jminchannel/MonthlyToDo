package xjj.derrew.xzgn

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import xjj.derrew.xzgn.manager.Language
import xjj.derrew.xzgn.manager.LanguageManager
import java.util.*

class MonthlyQuestJournalApplication : Application() {
    
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
            Language.ENGLISH -> Locale.ENGLISH
            Language.CHINESE_SIMPLIFIED -> Locale.SIMPLIFIED_CHINESE
            Language.CHINESE_TRADITIONAL -> Locale.TRADITIONAL_CHINESE
            Language.JAPANESE -> Locale.JAPANESE
            Language.INDONESIAN -> Locale("in", "ID")
        }
        
        val configuration = Configuration(base.resources.configuration)
        configuration.setLocale(locale)
        val context = base.createConfigurationContext(configuration)
        super.attachBaseContext(context)
    }
}
