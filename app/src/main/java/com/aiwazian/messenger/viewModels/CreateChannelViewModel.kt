package com.aiwazian.messenger.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.customType.ChannelType

class CreateChannelViewModel : ViewModel() {

    var channelName by mutableStateOf("")
        private set

    var channelBio by mutableStateOf("")
        private set

    var channelType by mutableStateOf<ChannelType>(ChannelType.PRIVATE)
        private set

    var onError: (() -> Unit)? = null

    fun changeChannelName(newName: String) {
        channelName = newName
    }

    fun changeChannelBio(newBio: String) {
        channelBio = newBio
    }

    fun createChannel() {
        if (channelName.isBlank()) {
            onError?.invoke()
            return
        }

        if (channelType == ChannelType.PUBLIC) {

        }
    }

}