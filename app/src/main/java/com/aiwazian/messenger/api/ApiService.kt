package com.aiwazian.messenger.api

import androidx.annotation.Keep
import com.aiwazian.messenger.data.AuthRequest
import com.aiwazian.messenger.data.AuthResponse
import com.aiwazian.messenger.data.CheckVerificationCodeRequest
import com.aiwazian.messenger.data.CreateChannelRequest
import com.aiwazian.messenger.data.DeleteChatRequest
import com.aiwazian.messenger.data.FcmTokenRequest
import com.aiwazian.messenger.data.FindUserRequest
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.RegisterRequest
import com.aiwazian.messenger.data.Session
import com.aiwazian.messenger.data.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

@Keep
interface ApiService {

    @POST("findUserByLogin")
    suspend fun findUserByLogin(@Body request: FindUserRequest): Response<ApiResponse>

    @POST("checkVerificationCode")
    suspend fun checkVerificationCode(@Body request: CheckVerificationCodeRequest): Response<ApiResponse>

    @POST("login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String)

    @GET("profile")
    @Keep
    suspend fun getProfile(@Header("Authorization") token: String): Response<User>

    @GET("contacts")
    suspend fun getContacts(@Header("Authorization") token: String): Response<List<User>>

    @GET("getSessions")
    suspend fun getSessions(@Header("Authorization") token: String): Response<List<Session>>

    @POST("updateFcmToken")
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Body newToken: FcmTokenRequest,
    ): Response<ApiResponse>

    @POST("terminateAllSessions")
    suspend fun terminateAllSessions(@Header("Authorization") token: String): Response<ApiResponse>

    @GET("getDeviceCount")
    suspend fun getDeviceCount(@Header("Authorization") token: String): Response<Int>

    @POST("deleteChat")
    suspend fun deleteChat(
        @Header("Authorization") token: String,
        @Body request: DeleteChatRequest,
    ): Response<ApiResponse>

    @GET("messages")
    suspend fun getMessagesBetweenUsers(
        @Header("Authorization") token: String,
        @retrofit2.http.Query("user1") user1: String,
        @retrofit2.http.Query("user2") user2: String,
    ): Response<List<Message>>

    @PUT("profileUpdate")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profile: User,
    ): Response<Unit>

    @GET("searchUser")
    suspend fun searchUser(
        @Header("Authorization") token: String,
        @retrofit2.http.Query("search") search: String,
    ): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<User>

    @POST("createChannel")
    suspend fun createChannel(
        @Header("Authorization") token: String,
        @Body requestBody: CreateChannelRequest
    ): Response<ApiResponse>
}