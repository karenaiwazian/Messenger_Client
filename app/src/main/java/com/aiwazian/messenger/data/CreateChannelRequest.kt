package com.aiwazian.messenger.data

import androidx.annotation.Keep

@Keep
data class CreateChannelRequest(
    @Keep val channelName: String,
    @Keep val channelBio: String
)
