package com.aiwazian.messenger

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.User
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
                    // Предполагается, что responseBody имеет тип User или может быть ему присвоен
                    user = responseBody
                    Log.d("UserManager", "User loaded: $user")
                } else {
                    Log.e("UserManager", "Response body is null despite successful response code")
                }
            } else {
                // Ошибка со стороны сервера (HTTP статус не 2xx)
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string() ?: "No error body"
                Log.e(
                    "UserManager",
                    "Failed to load user. HTTP Error: $errorCode - $errorBody"
                )
                // Здесь можно добавить специфическую обработку для разных кодов ошибок,
                // например, 401 (Unauthorized), 404 (Not Found) и т.д.
            }

        } catch (e: HttpException) {
            // Обработка ошибок HTTP, которые Retrofit выбрасывает как исключения
            // (хотя в вашем коде выше вы уже обрабатываете response.isSuccessful == false)
            // Этот блок может быть избыточным, если вы всегда проверяете response.isSuccessful,
            // но может быть полезен, если какая-то часть Retrofit или OkHttp решит бросить HttpException
            // до того, как вы сможете проверить isSuccessful.
            val errorCode = e.code()
            val errorMessage = e.response()?.errorBody()?.string() ?: e.message()
            Log.e(
                "UserManager",
                "HTTP exception while loading user. Code: $errorCode, Message: $errorMessage",
                e
            )
        } catch (e: SocketTimeoutException) {
            // Ошибка: таймаут соединения или чтения
            Log.e("UserManager", "Network timeout while loading user", e)
            // Здесь можно показать пользователю сообщение о проблемах с сетью
        } catch (e: IOException) {
            // Общая ошибка ввода-вывода, например, нет подключения к интернету,
            // DNS не найден, или другие проблемы сети.
            Log.e(
                "UserManager",
                "Network IO exception while loading user (e.g., no internet, DNS issue)",
                e
            )
            // Здесь можно показать пользователю сообщение о проблемах с сетью
        } catch (e: com.google.gson.JsonSyntaxException) {
            // Ошибка парсинга JSON (если вы используете Gson и ответ сервера невалидный JSON)
            // Убедитесь, что импортировали: import com.google.gson.JsonSyntaxException
            Log.e("UserManager", "JSON parsing error while loading user", e)
            // Это может указывать на несоответствие модели данных User и ответа сервера
        } catch (e: ClassCastException) {
            // Ошибка, которую вы упоминали ранее.
            // Связана с некорректным приведением типов, часто при работе с generics и рефлексией,
            // что может быть усугублено обфускацией, если не настроены правила ProGuard/R8.
            Log.e(
                "UserManager",
                "ClassCastException, possibly related to generics/reflection/obfuscation",
                e
            )
        } catch (e: Exception) {
            // Общий обработчик для всех остальных непредвиденных исключений
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