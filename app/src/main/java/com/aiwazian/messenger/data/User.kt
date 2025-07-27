package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class User(
    @Keep var id: Int = 0,
    @Keep var firstName: String = "",
    @Keep var lastName: String = "",
    @Keep var username: String = "",
    @Keep var bio: String = "",
)