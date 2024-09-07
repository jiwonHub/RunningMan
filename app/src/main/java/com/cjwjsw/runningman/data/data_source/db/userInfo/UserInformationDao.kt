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

    @Query("UPDATE user_info SET age = :age WHERE id = :id")
    suspend fun updateAge(id: String, age: Int)

    @Query("UPDATE user_info SET gender = :gender WHERE id = :id")
    suspend fun updateGender(id: String, gender: String)

    @Query("UPDATE user_info SET height = :height WHERE id = :id")
    suspend fun updateHeight(id: String, height: Int)

    @Query("UPDATE user_info SET weight = :weight WHERE id = :id")
    suspend fun updateWeight(id: String, weight: Int)
}