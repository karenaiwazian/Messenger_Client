package com.aiwazian.messenger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.aiwazian.messenger.ui.login.AuthScreen
import com.aiwazian.messenger.ui.theme.ApplicationTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ApplicationTheme {
                AuthScreen()
            }
        }
    }
}
