package com.aiwazian.messenger

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aiwazian.messenger.customType.Language
import com.aiwazian.messenger.customType.ThemeOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "data_store")

class DataStoreManager private constructor(private val context: Context) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: DataStoreManager? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                synchronized(this) {
                    if (INSTANCE == null) {
                        INSTANCE = DataStoreManager(context.applicationContext)
                    }
                }
            }
        }

        fun getInstance(): DataStoreManager {
            return INSTANCE ?: throw IllegalStateException("DataStoreManager is not initialized")
        }
    }

    private val THEME = stringPreferencesKey("app_theme")
    private val LANGUAGE = stringPreferencesKey("language")
    private val TOKEN = stringPreferencesKey("token")
    private val MESSAGE_TEXT_SIZE = stringPreferencesKey("message_text_size")
    private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    private val PRIMARY_COLOR_KEY = stringPreferencesKey("primary_color")

    suspend fun savePrimaryColor(colorName: String) {
        context.dataStore.edit { settings ->
            settings[PRIMARY_COLOR_KEY] = colorName
        }
    }

    fun getPrimaryColor() = context.dataStore.data.map { pref ->
        pref[PRIMARY_COLOR_KEY]
    }

    fun getMessageTextSize() = context.dataStore.data.map { pref ->
        pref[MESSAGE_TEXT_SIZE]
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { pref ->
            pref[TOKEN] = token
        }
    }

    fun getToken() = context.dataStore.data.map { pref ->
        pref[TOKEN]
    }

    fun removeToken() {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { pref ->
                pref[TOKEN] = ""
           }
        }
    }

    suspend fun saveLanguage(language: Language) {
        context.dataStore.edit { pref ->
            pref[LANGUAGE] = language.toString()
        }
    }

    fun getLanguage() = context.dataStore.data.map { pref ->
        return@map when (pref[LANGUAGE]) {
            Language.RU.toString() -> Language.RU
            else -> Language.EN
        }
    }

    suspend fun saveTheme(theme: ThemeOption) {
        context.dataStore.edit { pref ->
            pref[THEME] = theme.toString()
        }
    }

    fun getTheme() = context.dataStore.data.map { pref ->
        return@map when (pref[THEME]) {
            ThemeOption.DARK.toString() -> ThemeOption.DARK
            ThemeOption.LIGHT.toString() -> ThemeOption.LIGHT
            else -> ThemeOption.SYSTEM
        }
    }

    suspend fun setLanguage(context: Context, languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }

    fun getLanguageFlow(context: Context): Flow<String> {
        return context.dataStore.data
            .map { preferences ->
                preferences[LANGUAGE_KEY] ?: "en"
            }
    }
}