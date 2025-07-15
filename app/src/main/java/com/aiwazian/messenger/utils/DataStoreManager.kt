package com.aiwazian.messenger.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aiwazian.messenger.customType.Language
import com.aiwazian.messenger.customType.PrimaryColorOption
import com.aiwazian.messenger.customType.ThemeOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val USER_PREFERENCES_NAME = "data_store"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(USER_PREFERENCES_NAME)

private object Keys {
    val THEME = stringPreferencesKey("app_theme")
    val LANGUAGE = stringPreferencesKey("language")
    val TOKEN = stringPreferencesKey("token")
    val PRIMARY_COLOR = stringPreferencesKey("primary_color")
    val PASSCODE = stringPreferencesKey("passcode")
    val IS_LOCK_APP = booleanPreferencesKey("is_lock_app")
}

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

    private suspend fun <T> saveValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { settings ->
            settings[key] = value
        }
    }

    private fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return context.dataStore.data.map { pref ->
            pref[key] ?: defaultValue
        }
    }

    suspend fun saveToken(token: String)  = saveValue(Keys.TOKEN, token)
    suspend fun savePasscode(passcode: String) = saveValue(Keys.PASSCODE, passcode)
    suspend fun saveIsLockApp(isLock: Boolean) = saveValue(Keys.IS_LOCK_APP, isLock)
    suspend fun savePrimaryColor(colorName: String) = saveValue(Keys.PRIMARY_COLOR, colorName)
    suspend fun saveLanguage(language: Language) = saveValue(Keys.LANGUAGE, language.toString())
    suspend fun saveTheme(theme: ThemeOption) = saveValue(Keys.THEME, theme.toString())

    fun getToken() = getValue(Keys.TOKEN, "")
    fun getPasscode() = getValue(Keys.PASSCODE, "")
    fun getIsLockApp() = getValue(Keys.IS_LOCK_APP, false)
    fun getPrimaryColor() = getValue(Keys.PRIMARY_COLOR, PrimaryColorOption.Blue.name)
    fun getLanguage() = getValue(Keys.LANGUAGE, Language.EN.toString())
    fun getTheme() = getValue(Keys.THEME, ThemeOption.SYSTEM.name)
}
