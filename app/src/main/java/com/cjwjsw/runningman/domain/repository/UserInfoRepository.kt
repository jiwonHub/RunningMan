package com.cjwjsw.runningman.domain.repository

import com.cjwjsw.runningman.data.data_source.db.userInfo.UserInformationEntity

interface UserInfoRepository {

    suspend fun getAllUserInfo(): List<UserInformationEntity>

    suspend fun getUserInfo(id: String): UserInformationEntity

    suspend fun insertUserInfo(userInfo: UserInformationEntity)

    suspend fun getUserInfoCount(): Int

    suspend fun deleteUserInfo(id: String)

    suspend fun deleteAllUserInfo()

    suspend fun updateAge(id: String, age: Int)

    suspend fun updateGender(id: String, gender: String)

    suspend fun updateHeight(id: String, height: Int)

    suspend fun updateWeight(id: String, weight: Int)

}