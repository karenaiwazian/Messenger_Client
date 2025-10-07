package com.aiwazian.messenger.services

import com.aiwazian.messenger.api.RetrofitInstance
import com.aiwazian.messenger.data.ChannelInfo
import com.aiwazian.messenger.data.UserInfo
import javax.inject.Inject

class ChannelService @Inject constructor() {
    
    suspend fun save(channel: ChannelInfo): Int? {
        val response = RetrofitInstance.api.saveChannel(channel)
        return response.body()?.message?.toInt()
    }
    
    suspend fun delete(id: Int): Boolean {
        val response = RetrofitInstance.api.deleteChannel(id)
        return response.isSuccessful
    }
    
    suspend fun get(id: Int): ChannelInfo? {
        val response = RetrofitInstance.api.getChannel(id)
        return response.body()
    }
    
    suspend fun join(id: Int): Boolean {
        val response = RetrofitInstance.api.joinChannel(id)
        return response.isSuccessful
    }
    
    suspend fun leave(id: Int): Boolean {
        val response = RetrofitInstance.api.leaveChannel(id)
        return response.isSuccessful
    }
    
    suspend fun isBusyPublicLick(link:String): Boolean {
        val response = RetrofitInstance.api.checkChannelPublicLink(link)
        return !response.isSuccessful
    }
    
    suspend fun getSubscribers(id:Int): List<UserInfo>? {
        val response = RetrofitInstance.api.getChannelSubscribers(id)
        return response.body()
    }
    
}