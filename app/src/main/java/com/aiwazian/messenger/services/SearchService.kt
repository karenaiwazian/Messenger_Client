package com.aiwazian.messenger.services

import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import javax.inject.Inject

class SearchService @Inject constructor() {
    
    suspend fun searchUserByUsername(query: String): List<User>? {
        val response = RetrofitInstance.api.searchUser(query)
        return response.body()
    }
}
