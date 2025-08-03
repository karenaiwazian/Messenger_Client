package com.aiwazian.messenger

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.services.AppLockService
import com.aiwazian.messenger.services.NotificationService
import com.aiwazian.messenger.services.ThemeService
import com.aiwazian.messenger.services.TokenManager
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.ui.ChatScreen
import com.aiwazian.messenger.ui.LockScreen
import com.aiwazian.messenger.ui.MainScreen
import com.aiwazian.messenger.ui.element.NavigationController
import com.aiwazian.messenger.ui.theme.ApplicationTheme
import com.aiwazian.messenger.utils.DataStoreManager
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.viewModels.NavigationViewModel
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var appLockService: AppLockService
    private lateinit var themeService: ThemeService

    override fun attachBaseContext(newBase: Context) {
        DataStoreManager.initialize(newBase)
        val dataStoreManager = DataStoreManager.getInstance()

        val savedLanguageCode = runBlocking {
            TokenManager.init()
            dataStoreManager.getLanguage().first().lowercase()
        }

        val locale = Locale(savedLanguageCode)
        Locale.setDefault(locale)

        val config = Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)

        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)

        TokenManager.setUnauthorizedCallback {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            this@MainActivity.startActivity(intent)
        }

        appLockService = AppLockService()
        themeService = ThemeService()

        setContent {
            val isLockApp by appLockService.isLockApp.collectAsState()
            val selectedTheme by themeService.currentTheme.collectAsState()
            val selectedColor by themeService.primaryColor.collectAsState()
            val isDynamicColorEnable by themeService.dynamicColor.collectAsState()

            LaunchedEffect(Unit) {
                try {
                    val notificationService = NotificationService()
                    val token = notificationService.getFirebaseToken()
                    notificationService.sendTokenToServer(token)
                } catch (e: Exception) {
                    Log.e(
                        "MainActivity",
                        "Error while try send notification token to server" + e.message.toString()
                    )
                }

                try {
                    WebSocketManager.onConnect = {
                        lifecycleScope.launch {
                            UserService.loadUserData()
                        }
                    }
                    WebSocketManager.onClose = { code ->
                        if (code == 1008) {
                            TokenManager.getUnauthorizedCallback()?.invoke()
                        } else {
                            lifecycleScope.launch {
                                delay(1000)
                                WebSocketManager.connect()
                            }
                        }
                    }

                    WebSocketManager.onFailure = {
                        lifecycleScope.launch {
                            delay(1000)
                            WebSocketManager.connect()
                        }
                    }

                    WebSocketManager.connect()
                } catch (e: Exception) {
                    Log.e("MainActivity", e.message.toString())
                }
            }

            ApplicationTheme(
                theme = selectedTheme,
                dynamicColor = isDynamicColorEnable,
                primaryColor = selectedColor.color
            ) {
                val navViewModel: NavigationViewModel = viewModel()

                if (!isLockApp) {
                    NavigationController(startScreen = {
                        MainScreen()
                    })
                }

                AnimatedVisibility(
                    visible = isLockApp,
                    enter = fadeIn(tween(100)),
                    exit = fadeOut(tween(100))
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
