package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class FcmTokenRequest (
    @Keep val token: String
)