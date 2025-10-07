package com.aiwazian.messenger.viewModels

import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.UserHandle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.aiwazian.messenger.services.DialogController

class DataUsageViewModel : ViewModel() {
    
    val clearCacheDialog = DialogController()
    
    var cacheSize by mutableLongStateOf(0)
        private set
    
    var appSize by mutableLongStateOf(0)
        private set
    
    fun reload(context: Context) {
        getCacheSize(context)
        getAppSize(context)
    }
    
    private fun getCacheSize(context: Context) {
        val storageStatsManager =
            context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        val appInfo = context.applicationInfo
        val user = UserHandle.getUserHandleForUid(appInfo.uid)
        
        try {
            val stats =
                storageStatsManager.queryStatsForPackage(
                    appInfo.storageUuid,
                    context.packageName,
                    user
                )
            cacheSize = stats.cacheBytes
        } catch (e: Exception) {
            cacheSize = 0
        }
    }
    
    private fun getAppSize(context: Context) {
        val storageStatsManager =
            context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
        val appInfo = context.applicationInfo
        val user = UserHandle.getUserHandleForUid(appInfo.uid)
        
        try {
            val stats =
                storageStatsManager.queryStatsForPackage(
                    appInfo.storageUuid,
                    context.packageName,
                    user
                )
            appSize = stats.appBytes + stats.dataBytes + stats.cacheBytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}