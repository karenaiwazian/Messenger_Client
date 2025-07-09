package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class AuthResponse(
    @Keep val message: String?,
    @Keep val token: String?,
)
