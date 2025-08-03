package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.services.UserService
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.services.TokenManager
import kotlinx.coroutines.launch

class UserSearchViewModel : ViewModel() {

    var searchResults = mutableStateListOf<User>()
        private set

    var query by mutableStateOf("")
        private set

    suspend fun searchUsersByPrefix(prefix: String) {
        if (prefix.isBlank()) {
            searchResults.clear()
            query = ""
            return
        }

        query = prefix.trim()

        try {
            val tokenManager = TokenManager()
            val token = tokenManager.getToken()
            val response = RetrofitInstance.api.searchUser(token, query)

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
