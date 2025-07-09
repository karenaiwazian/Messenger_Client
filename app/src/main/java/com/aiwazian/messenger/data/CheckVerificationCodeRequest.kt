package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class CheckVerificationCodeRequest(
    @Keep val login: String,
    @Keep val code: String
)
