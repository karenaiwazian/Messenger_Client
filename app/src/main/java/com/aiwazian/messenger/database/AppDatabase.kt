package com.aiwazian.messenger.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aiwazian.messenger.database.dao.AccountDao
import com.aiwazian.messenger.database.dao.ChannelDao
import com.aiwazian.messenger.database.dao.FolderChatDao
import com.aiwazian.messenger.database.dao.FolderDao
import com.aiwazian.messenger.database.dao.UserDao
import com.aiwazian.messenger.database.entity.AccountEntity
import com.aiwazian.messenger.database.entity.ChannelEntity
import com.aiwazian.messenger.database.entity.FolderChatEntity
import com.aiwazian.messenger.database.entity.FolderEntity
import com.aiwazian.messenger.database.entity.MessageEntity
import com.aiwazian.messenger.database.entity.UserEntity

@Database(
    entities = [FolderEntity::class, FolderChatEntity::class, UserEntity::class, MessageEntity::class, ChannelEntity::class, AccountEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun folderDao(): FolderDao
    
    abstract fun folderChatDao(): FolderChatDao
    
    abstract fun userDao(): UserDao
    
    abstract fun channelDao(): ChannelDao
    
    abstract fun accountDao(): AccountDao
}