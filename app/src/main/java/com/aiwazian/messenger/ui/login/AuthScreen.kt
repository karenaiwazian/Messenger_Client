package com.aiwazian.messenger.ui.login

import android.app.Activity
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aiwazian.messenger.viewModels.AuthViewModel
import androidx.navigation.compose.NavHost
import com.aiwazian.messenger.MainActivity
import com.aiwazian.messenger.utils.Screen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AuthScreen() {
    Content()
}

@Composable
private fun Content() {
    val navController = rememberNavController()

    val transition = tween<IntOffset>(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )

    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        enterTransition = {
            slideInHorizontally(transition) { it }
        },
        exitTransition = {
            slideOutHorizontally(animationSpec = transition) { -it }
        },
        popEnterTransition = {
            slideInHorizontally(animationSpec = transition) { -it }
        },
        popExitTransition = {
            slideOutHorizontally(animationSpec = transition) { it }
        }
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        composable(route = Screen.Verification.route) {
            VerificationCodeScreen(navController = navController, viewModel = authViewModel)
        }
        composable(route = Screen.Password.route) {
            PasswordScreen(navController = navController, viewModel = authViewModel)
        }
    }
}
