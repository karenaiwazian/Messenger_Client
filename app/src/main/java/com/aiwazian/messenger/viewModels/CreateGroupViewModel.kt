package com.aiwazian.messenger.viewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class CreateGroupViewModel : ViewModel() {

    var groupName by mutableStateOf("")
        private set

    var onError: (() -> Unit)? = null

    fun changeGroupName(newGroupName: String) {
        groupName = newGroupName
    }

    fun createGroup() {
        if (groupName.isBlank()) {
            onError?.invoke()
            return
        }
    }
}