package com.aiwazian.messenger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.lifecycleScope
import com.aiwazian.messenger.customType.PrimaryColorOption
import com.aiwazian.messenger.customType.ThemeOption
import com.aiwazian.messenger.ui.ChatScreen
import com.aiwazian.messenger.ui.MainScreen
import com.aiwazian.messenger.ui.theme.ApplicationTheme
import com.aiwazian.messenger.ui.theme.LocalCustomColors
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.properties.Delegates
import java.util.Locale

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        FirebaseApp.initializeApp(this)
        DataStoreManager.initialize(this)
        val dataStoreManager = DataStoreManager.getInstance()

        lifecycleScope.launch {
            val language = dataStoreManager.getLanguage().first()
            val languageHelper = LanguageHelper(context = this@MainActivity)
            languageHelper.selLanguage(
                languageCode = language.toString().lowercase()
            )
        }

        setContent {
            val context = LocalContext.current

            var isLoading by remember { mutableStateOf(true) }
            var isLoggedIn by remember { mutableStateOf(false) }

            val theme by dataStoreManager.getTheme().collectAsState(initial = ThemeOption.SYSTEM)

            val colorName by dataStoreManager.getPrimaryColor()
                .collectAsState(initial = PrimaryColorOption.Blue.name)

            val selectedColor = try {
                PrimaryColorOption.valueOf(colorName ?: PrimaryColorOption.Blue.name)
            } catch (e: Exception) {
                PrimaryColorOption.Blue
            }

            LaunchedEffect(Unit) {
                try {
                    val token = dataStoreManager.getToken().firstOrNull()

                    if (token.isNullOrBlank()) {
                        dataStoreManager.removeToken()
                        val intent = Intent(context, LoginActivity::class.java)
                        if (context !is Activity) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }

                        context.startActivity(intent)
                        (context as Activity).finish()
                    }

                    try {
                        UserManager.loadUserData()

                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result
                                MyFirebaseMessagingService.sendTokenToServer(token)
                                Log.e("FCM", "success $token")
                            } else {
                                Log.e("FCM", "Error ${task.exception}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", e.message.toString())
                    }

                    try {
                        WebSocketManager.init(this@MainActivity)
                        WebSocketManager.connect()
                        WebSocketManager.onConnect = {
                            launch {
                                UserManager.loadUserData()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", e.message.toString())
                    }

                    isLoggedIn = true
                    isLoading = false

                } catch (e: Exception) {
                    Log.e("MainActivity", e.message.toString())
                }
            }

            ApplicationTheme(theme = theme, primaryColor = selectedColor.color) {
                val colors = LocalCustomColors.current

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colors.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colors.primary,
                            modifier = Modifier.background(colors.secondary)
                        )
                    }
                } else if (isLoggedIn) {
                    SwipeStackApp()

                    LaunchedEffect(Unit) {
                        val chatId = intent.getStringExtra("chatId")
                        if (chatId != null) {
                            addScreenInStack { ChatScreen(chatId) }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val chatId = intent.getStringExtra("chatId")
        if (chatId != null) {
            addScreenInStack { ChatScreen(chatId) }
        }
    }
}

private lateinit var screenStack: SnapshotStateList<@Composable (() -> Unit)>
private lateinit var offsetStack: SnapshotStateList<Animatable<Float, AnimationVector1D>>
private lateinit var scope: CoroutineScope
private var screenWidthPx by Delegates.notNull<Float>()

private const val tweenDurationMillis: Int = 200

@Composable
private fun SwipeStackApp() {
    screenStack = remember { mutableStateListOf<@Composable () -> Unit>() }
    offsetStack = remember { mutableStateListOf<Animatable<Float, AnimationVector1D>>() }
    scope = rememberCoroutineScope()
    screenWidthPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenWidthDp.dp.toPx()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val colors = LocalCustomColors.current

    Box(
        Modifier
            .fillMaxSize()
            .background(colors.secondary)
    ) {
        MainScreen()

        screenStack.forEachIndexed { index, screenContent ->
            val offsetX = offsetStack[index]
            val isTop = index == screenStack.lastIndex

            BackHandler(enabled = screenStack.isNotEmpty()) {
                if (screenStack.isNotEmpty()) {
                    removeLastScreenFromStack()
                }
            }

            LaunchedEffect(screenContent) {
                offsetX.animateTo(0f, tween(tweenDurationMillis))
            }

            val backgroundAlpha = ((1f - (offsetX.value / screenWidthPx)).coerceIn(0f, 1f)) * 0.5f

            BoxShadow(index, backgroundAlpha)

            Box(
                Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .background(colors.secondary)
                    .zIndex(index + 0.2f)
                    .then(
                        if (isTop) Modifier.draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                keyboardController?.hide()
                                scope.launch {
                                    offsetX.snapTo((offsetX.value + delta).coerceAtLeast(0f))
                                }
                            },
                            onDragStopped = {
                                scope.launch {
                                    if (offsetX.value > screenWidthPx / 4) {
                                        removeLastScreenFromStack()
                                    } else {
                                        offsetX.animateTo(0f, tween(tweenDurationMillis))
                                    }
                                }
                            }
                        ) else Modifier
                    )
            ) {
                screenContent()
            }
        }
    }
}

fun addScreenInStack(screen: @Composable () -> Unit) {
    screenStack.add(screen)
    offsetStack.add(Animatable(screenWidthPx))
}

fun removeLastScreenFromStack() {
    if (screenStack.isEmpty()) {
        return
    }

    scope.launch {
        val topOffset = offsetStack[screenStack.lastIndex]
        topOffset.animateTo(screenWidthPx, tween(tweenDurationMillis))
        topOffset.snapTo(screenWidthPx)
        screenStack.removeAt(screenStack.lastIndex)
        offsetStack.removeAt(offsetStack.lastIndex)
    }
}

@Composable
private fun BoxShadow(index: Int, backgroundAlpha: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = backgroundAlpha))
            .zIndex(index + 0.1f)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { }
    )
}
