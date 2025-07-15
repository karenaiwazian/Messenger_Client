package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.utils.UserManager
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import kotlinx.coroutines.launch

class UserSearchViewModel : ViewModel() {

    var searchResults = mutableStateListOf<User>()
        private set

    var query by mutableStateOf("")
        private set

    fun searchUsersByPrefix(prefix: String) {
        if (prefix.isBlank()) {
            searchResults.clear()
            return
        }

        query = prefix

        viewModelScope.launch {
            try {
                val token = UserManager.token
                val response = RetrofitInstance.api.searchUser("Bearer $token", query)

                if (response.isSuccessful) {
                    searchResults.clear()
                    searchResults.addAll(response.body() ?: emptyList())
                } else {
                    searchResults.clear()
                }
            } catch (e: Exception) {
                Log.e("SearchViewModel", "${e.message}")
            }
        }
    }
}
