package com.aiwazian.messenger.services

import android.content.Context
import android.content.res.Configuration
import com.aiwazian.messenger.customType.Language
import com.aiwazian.messenger.utils.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class LanguageService(private val context: Context) {
    private var _languageCode = MutableStateFlow(Language.EN)
    var languageCode = _languageCode.asStateFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val dataStorage = DataStoreManager.Companion.getInstance()

    init {
        coroutineScope.launch {
            dataStorage.getLanguage().collectLatest { languageName ->
                _languageCode.value = Language.Companion.fromString(languageName)
            }
        }
    }

    fun selLanguage(language: Language) {
        val languageCode = language.toString().lowercase()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    suspend fun saveLanguage(language: Language) {
        dataStorage.saveLanguage(language)
    }
}