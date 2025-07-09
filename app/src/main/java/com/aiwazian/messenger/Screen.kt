package com.aiwazian.messenger

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Verification : Screen("verification")
    object Password : Screen("password")
}