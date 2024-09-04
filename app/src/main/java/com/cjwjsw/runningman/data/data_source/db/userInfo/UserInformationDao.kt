package com.cjwjsw.runningman.data.data_source.db.userInfo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserInformationDao {

    @Query("SELECT * FROM user_info")
    suspend fun getAllUserInfo(): List<UserInformationEntity>

    @Query("SELECT * FROM user_info WHERE id = :id")
    suspend fun getUserInfo(id: String): UserInformationEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userInfo: UserInformationEntity)

    @Query("SELECT COUNT(*) FROM user_info")
    suspend fun getUserInfoCount(): Int

    @Query("DELETE FROM user_info WHERE id = :id")
    suspend fun deleteUserInfo(id: String)

    @Query("DELETE FROM user_info")
    suspend fun deleteAllUserInfo()
}