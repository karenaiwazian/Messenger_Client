package com.aiwazian.messenger.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aiwazian.messenger.database.entity.ChannelEntity

@Dao
interface ChannelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channelEntity: ChannelEntity)
    
    @Query("SELECT * FROM channel")
    suspend fun getAll(): List<ChannelEntity>
    
    @Query("SELECT * FROM channel WHERE id = :id")
    suspend fun get(id: Int): ChannelEntity?
    
    @Update
    suspend fun update(channelEntity: ChannelEntity)
    
    @Delete
    suspend fun delete(channelEntity: ChannelEntity)
}