package com.aiwazian.messenger

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

class LanguageHelper(private val context: Context) {
    
    fun selLanguage(languageCode: String){
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}