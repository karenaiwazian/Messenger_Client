package com.aiwazian.messenger.utils

object Constants {
    private const val SERVER_IP = "192.168.1.106" //109.68.212.178
    private const val SERVER_PORT = 3003
    private const val WEB_SOCKET_PORT = 8080
    const val SERVER_URL = "http://${SERVER_IP}:${SERVER_PORT}"
    const val WEB_SOCKET_URL = "ws://${SERVER_IP}:${WEB_SOCKET_PORT}"
    const val MAX_LENGTH_PASSCODE = 4
}