package com.aiwazian.messenger.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    
    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults = _searchResults.asStateFlow()
    
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()
    
    fun onQueryChange(newQuery: String) {
        _query.value = newQuery.trim()
        
        if (_query.value.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            search()
        }
    }
    
    private suspend fun search() {
        try {
            val response = RetrofitInstance.api.searchUser(_query.value)
            
            if (response.isSuccessful) {
                _searchResults.value = emptyList()
                _searchResults.value = response.body() ?: emptyList()
            } else {
                _searchResults.value = emptyList()
            }
        } catch (e: Exception) {
            Log.e(
                "SearchViewModel",
                "${e.message}"
            )
        }
    }
}
