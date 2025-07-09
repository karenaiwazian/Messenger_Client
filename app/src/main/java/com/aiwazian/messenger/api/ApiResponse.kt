package com.aiwazian.messenger.api

import androidx.annotation.Keep

@Keep
data class ApiResponse (
    @Keep val success: Boolean,
    @Keep val message: String
)