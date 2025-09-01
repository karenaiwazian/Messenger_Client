package com.aiwazian.messenger.utils
import com.aiwazian.messenger.BuildConfig

object Constants {
    private const val SERVER_IP = BuildConfig.SERVER_IP
    private const val SERVER_PORT = 3000
    private const val WEB_SOCKET_PORT = 8080
    const val SERVER_URL = "http://${SERVER_IP}:${SERVER_PORT}"
    const val WEB_SOCKET_URL = "ws://${SERVER_IP}:${WEB_SOCKET_PORT}"
    const val DOMAIN_NAME = ""
}