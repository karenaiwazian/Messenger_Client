package com.aiwazian.messenger.interfaces

import androidx.annotation.Keep
import com.aiwazian.messenger.data.ApiResponse
import com.aiwazian.messenger.data.AuthRequest
import com.aiwazian.messenger.data.AuthResponse
import com.aiwazian.messenger.data.ChatFolder
import com.aiwazian.messenger.data.ChatInfo
import com.aiwazian.messenger.data.CheckVerificationCodeRequest
import com.aiwazian.messenger.data.DeleteChatRequest
import com.aiwazian.messenger.data.NotificationTokenRequest
import com.aiwazian.messenger.data.FindUserRequest
import com.aiwazian.messenger.data.Message
import com.aiwazian.messenger.data.RegisterRequest
import com.aiwazian.messenger.data.Session
import com.aiwazian.messenger.data.User
import com.aiwazian.messenger.utils.Route
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

@Keep
interface ApiService {

    @POST(Route.FIND_USER_BY_LOGIN)
    suspend fun findUserByLogin(@Body request: FindUserRequest): Response<ApiResponse>

    @POST("checkVerificationCode")
    suspend fun checkVerificationCode(@Body request: CheckVerificationCodeRequest): Response<ApiResponse>

    @POST(Route.LOGIN)
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST(Route.REGISTER)
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST(Route.LOGOUT)
    suspend fun logout(@Header("Authorization") token: String)

    @GET(Route.ME)
    suspend fun getMe(@Header("Authorization") token: String): Response<User>

    @GET(Route.UNARCHIVED_CHATS)
    suspend fun getUnarchivedChats(@Header("Authorization") token: String): Response<List<ChatInfo>>

    @GET(Route.ARCHIVED_CHATS)
    suspend fun getArchivedChats(@Header("Authorization") token: String): Response<List<ChatInfo>>

    @POST(Route.PIN_CHAT)
    suspend fun pinChat(
        @Header("Authorization") token: String,
        @Body chatInfo: ChatInfo
    ): Response<ApiResponse>

    @POST(Route.UNPIN_CHAT)
    suspend fun unpinChat(
        @Header("Authorization") token: String,
        @Body chatInfo: ChatInfo
    ): Response<ApiResponse>

    @GET(Route.GET_SESSIONS)
    suspend fun getSessions(@Header("Authorization") token: String): Response<List<Session>>

    @POST(Route.UPDATE_FCM_TOKEN)
    suspend fun updateFcmToken(
        @Header("Authorization") token: String,
        @Body newToken: NotificationTokenRequest
    ): Response<ApiResponse>

    @POST(Route.TERMINATE_ALL_SESSIONS)
    suspend fun terminateAllSessions(@Header("Authorization") token: String): Response<ApiResponse>

    @GET(Route.GET_DEVICE_COUNT)
    suspend fun getDeviceCount(@Header("Authorization") token: String): Response<Int>

    @POST(Route.DELETE_CHAT)
    suspend fun deleteChat(
        @Header("Authorization") token: String,
        @Body request: DeleteChatRequest
    ): Response<ApiResponse>

    @GET(Route.MESSAGES)
    suspend fun getMessagesBetweenUsers(
        @Header("Authorization") token: String,
        @Query("user") user: Int
    ): Response<List<Message>>

    @PUT(Route.PROFILE_UPDATE)
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body profile: User
    ): Response<Unit>

    @GET(Route.SEARCH_USER)
    suspend fun searchUser(
        @Header("Authorization") token: String,
        @Query("search") search: String
    ): Response<List<User>>

    @GET(Route.GE_USER_BY_ID)
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<User>

    @POST(Route.ADD_CHAT_TO_ARCHIVE)
    suspend fun archiveChat(
        @Header("Authorization") token: String,
        @Body requestBody: ChatInfo
    ): Response<ApiResponse>

    @POST(Route.DELETE_CHAT_FROM_ARCHIVE)
    suspend fun unarchiveChat(
        @Header("Authorization") token: String,
        @Body requestBody: ChatInfo
    ): Response<ApiResponse>

    @POST(Route.SEND_MESSAGE)
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Body requestBody: Message
    ): Response<ApiResponse>

    @POST(Route.FOLDER)
    suspend fun saveFolder(
        @Header("Authorization") token: String,
        @Body requestBody: ChatFolder
    ): Response<ApiResponse>

    @DELETE(Route.DELETE_FOLDER)
    suspend fun deleteFolder(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ApiResponse>

    @GET(Route.GET_FOLDER_CHATS)
    suspend fun getFolderChats(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<List<ChatInfo>>

    @GET(Route.FOLDERS)
    suspend fun getFolders(@Header("Authorization") token: String): Response<List<ChatFolder>>

    @GET(Route.CHATS)
    suspend fun getAllChats(@Header("Authorization") token: String): Response<List<ChatInfo>>
}
