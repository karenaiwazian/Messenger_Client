package com.aiwazian.messenger.utils

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object UserManager {

    var user by mutableStateOf(User())
        private set

    var token by mutableStateOf("")
        private set

    fun updateUser(updatedUser: User) {
        user = updatedUser
    }

    suspend fun loadUserData() {
        val dataStoreManager = DataStoreManager.getInstance()
        token = dataStoreManager.getToken().firstOrNull().toString()

        try {
            val response = RetrofitInstance.api.getProfile("Bearer $token")

            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    user = responseBody
                    Log.d("UserManager", "User loaded: $user")
                } else {
                    Log.e("UserManager", "Response body is null despite successful response code")
                }
            } else {
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.e(
                    "UserManager",
                    "Failed to load user. HTTP Error: $errorCode - $errorBody"
                )
            }

        } catch (e: HttpException) {
            val errorCode = e.code()
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e(
                "UserManager",
                "HTTP exception while loading user. Code: $errorCode, Message: $errorMessage",
                e
            )
        } catch (e: SocketTimeoutException) {
            Log.e("UserManager", "Network timeout while loading user", e)
        } catch (e: IOException) {
            Log.e(
                "UserManager",
                "Network IO exception while loading user (e.g., no internet, DNS issue)",
                e
            )
        } catch (e: JsonSyntaxException) {
            Log.e("UserManager", "JSON parsing error while loading user", e)
        } catch (e: ClassCastException) {
            Log.e(
                "UserManager",
                "ClassCastException, possibly related to generics/reflection/obfuscation",
                e
            )
        } catch (e: Exception) {
            Log.e("UserManager", "An unexpected error occurred while loading user", e)
        }
    }

    fun saveUserData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.updateProfile("Bearer $token", user)
                if (response.isSuccessful) {
                    Log.d("UserManager", "change is success$user")
                }
            } catch (e: Exception) {
                Log.e("UserManager", "Error saving user data", e)
            }
        }
    }
}