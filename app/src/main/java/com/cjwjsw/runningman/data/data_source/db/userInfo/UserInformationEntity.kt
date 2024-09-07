package com.cjwjsw.runningman.data.data_source.db.userInfo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_info")
data class UserInformationEntity(
    @PrimaryKey val id: String,
    val age: Int,
    val gender: String,
    val height: Int,
    val weight: Int
)
