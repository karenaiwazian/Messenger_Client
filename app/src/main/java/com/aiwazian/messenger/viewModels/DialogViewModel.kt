package com.aiwazian.messenger.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class DialogViewModel : ViewModel() {
    private val _isDialogVisible = mutableStateOf(false)

    val isDialogVisible: State<Boolean> get() = _isDialogVisible

    var primaryAction: (() -> Unit)? = null

    var dismissAction: (() -> Unit)? = null

    fun showDialog() {
        _isDialogVisible.value = true
    }

    fun hideDialog() {
        _isDialogVisible.value = false
    }
}