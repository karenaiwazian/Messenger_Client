package com.aiwazian.messenger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.customType.Language
import com.aiwazian.messenger.customType.ThemeOption
import com.aiwazian.messenger.ui.ChatScreen
import com.aiwazian.messenger.ui.LockScreen
import com.aiwazian.messenger.ui.MainScreen
import com.aiwazian.messenger.ui.element.NavigationController
import com.aiwazian.messenger.ui.theme.ApplicationTheme
import com.aiwazian.messenger.utils.AppLockService
import com.aiwazian.messenger.utils.DataStoreManager
import com.aiwazian.messenger.utils.LanguageService
import com.aiwazian.messenger.utils.NotificationService
import com.aiwazian.messenger.utils.ThemeService
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.viewModels.NavigationViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var appLockService: AppLockService
    private lateinit var themeService: ThemeService
    private lateinit var dataStoreManager: DataStoreManager

    override fun attachBaseContext(newBase: Context) {
        DataStoreManager.initialize(newBase)
        val dataStoreManager = DataStoreManager.getInstance()

        val savedLanguageCode = runBlocking {
            dataStoreManager.getLanguage().first().lowercase()
        }

        val locale = Locale(savedLanguageCode)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)

        super.attachBaseContext(context)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        appLockService = AppLockService()
        themeService = ThemeService()
        dataStoreManager = DataStoreManager.getInstance()

        setContent {
            val isLockApp by appLockService.isLockApp.collectAsState()
            val selectedTheme by themeService.currentTheme.collectAsState()
            val selectedColor by themeService.primaryColor.collectAsState()

            LaunchedEffect(Unit) {
                try {
                    val token = dataStoreManager.getToken().first()

                    if (token.isBlank()) {
                        dataStoreManager.saveToken("")

                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", e.message.toString())
                }

                try {
                    val notificationService = NotificationService()
                    notificationService.sendTokenToServer()
                } catch (e: Exception) {
                    Log.e(
                        "MainActivity",
                        "Error while try send notification token to server" + e.message.toString()
                    )
                }

                try {
                    UserManager.loadUserData()
                    WebSocketManager.init(this@MainActivity)
                    WebSocketManager.onConnect = {
                        lifecycleScope.launch {
                            UserManager.loadUserData()
                        }
                    }
                    WebSocketManager.connect()
                } catch (e: Exception) {
                    Log.e("MainActivity", e.message.toString())
                }
            }

            ApplicationTheme(theme = selectedTheme, primaryColor = selectedColor.color) {
                val navViewModel: NavigationViewModel = viewModel()

                if (!isLockApp) {
                    NavigationController(startScreen = {
                        MainScreen()
                    })
                }

                AnimatedVisibility(
                    visible = isLockApp,
                    enter = fadeIn(animationSpec = tween(durationMillis = 100)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 100))
                ) {
                    LockScreen()
                }

                LaunchedEffect(Unit) {
                    val chatId = intent.getStringExtra("chatId")?.toIntOrNull()

                    if (chatId != null) {
                        navViewModel.addScreenInStack {
                            ChatScreen(chatId)
                        }
                    }
                }
            }
        }
    }
}
