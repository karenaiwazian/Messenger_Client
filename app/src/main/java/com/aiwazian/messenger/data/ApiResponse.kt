package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class ApiResponse (
    @Keep val success: Boolean,
    @Keep val message: String
)