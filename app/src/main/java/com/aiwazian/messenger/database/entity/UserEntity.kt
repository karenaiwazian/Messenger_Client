package com.aiwazian.messenger.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user")
data class UserEntity(
    @PrimaryKey var id: Int,
    var firstName: String = "",
    var lastName: String = "",
    var username: String? = null,
    var bio: String = "",
    var dateOfBirth: Long? = null
)