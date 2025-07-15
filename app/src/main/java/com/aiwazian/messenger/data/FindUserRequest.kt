package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class FindUserRequest(
    @Keep val login: String
)
