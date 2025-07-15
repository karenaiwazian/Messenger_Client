package com.aiwazian.messenger

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aiwazian.messenger.ui.ChatScreen
import com.aiwazian.messenger.ui.LockScreen
import com.aiwazian.messenger.ui.MainScreen
import com.aiwazian.messenger.ui.element.NavigationController
import com.aiwazian.messenger.ui.theme.ApplicationTheme
import com.aiwazian.messenger.utils.DataStoreManager
import com.aiwazian.messenger.utils.LanguageService
import com.aiwazian.messenger.utils.NotificationService
import com.aiwazian.messenger.utils.ThemeService
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.utils.WebSocketManager
import com.aiwazian.messenger.viewModels.NavigationViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)
        DataStoreManager.initialize(this)

        val dataStoreManager = DataStoreManager.getInstance()
        var isLockApp by mutableStateOf<Boolean?>(null)

        lifecycleScope.launch {
            dataStoreManager.getIsLockApp().collect {
                val passcode = dataStoreManager.getPasscode().first()

                isLockApp = if (passcode.isNotBlank()) {
                    it
                } else {
                    false
                }
            }
        }

        setContent {
            val context = LocalContext.current

            val languageService = LanguageService(this@MainActivity)
            val language = languageService.languageCode.collectAsState().value
            languageService.selLanguage(language)

            val themeService = ThemeService()
            val selectedTheme = themeService.currentTheme.collectAsState().value
            val selectedColor = themeService.primaryColor.collectAsState().value

            LaunchedEffect(Unit) {
                try {
                    val token = dataStoreManager.getToken().firstOrNull()

                    if (token.isNullOrBlank()) {
                        dataStoreManager.saveToken("")

                        val intent = Intent(context, LoginActivity::class.java)

                        if (context !is Activity) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }

                        context.startActivity(intent)
                        (context as? Activity)?.finish()
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", e.message.toString())
                }

                try {
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result

                            val notificationService = NotificationService()

                            notificationService.sendTokenToServer(token)

                            Log.e("FCM", "success $token")
                        } else {
                            Log.e("FCM", "Error ${task.exception}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", e.message.toString())
                }

                try {
                    UserManager.loadUserData()
                    WebSocketManager.init(this@MainActivity)
                    WebSocketManager.connect()
                } catch (e: Exception) {
                    Log.e("MainActivity", e.message.toString())
                }
            }

            ApplicationTheme(theme = selectedTheme, primaryColor = selectedColor.color) {
                val navViewModel: NavigationViewModel = viewModel()

                if (isLockApp != null) {
                    NavigationController(startScreen = {
                        MainScreen()
                    })
                }

                AnimatedVisibility(
                    visible = isLockApp == true,
                    enter = fadeIn(animationSpec = tween(durationMillis = 100)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 100))
                ) {
                    LockScreen()
                }

                LaunchedEffect(Unit) {
                    val chatId = intent.getStringExtra("chatId")

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

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//
//        val chatId = intent.getStringExtra("chatId")
//
//        if (chatId != null) {
//            navViewModel.addScreenInStack {
//                ChatScreen(chatId)
//            }
//        }
//    }
