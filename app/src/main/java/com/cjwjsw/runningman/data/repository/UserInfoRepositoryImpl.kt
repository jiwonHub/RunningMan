package com.cjwjsw.runningman.data.repository

import com.cjwjsw.runningman.data.data_source.db.userInfo.UserInformationDao
import com.cjwjsw.runningman.data.data_source.db.userInfo.UserInformationEntity
import com.cjwjsw.runningman.domain.repository.UserInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserInfoRepositoryImpl @Inject constructor(
    private val userInformationDao: UserInformationDao
) : UserInfoRepository {
    override suspend fun getAllUserInfo(): List<UserInformationEntity> = withContext(Dispatchers.IO) {
        userInformationDao.getAllUserInfo()
    }

    override suspend fun getUserInfo(id: String): UserInformationEntity = withContext(Dispatchers.IO) {
        userInformationDao.getUserInfo(id)
    }

    override suspend fun insertUserInfo(userInfo: UserInformationEntity) = withContext(Dispatchers.IO) {
        userInformationDao.insertUserInfo(userInfo)
    }

    override suspend fun getUserInfoCount(): Int = withContext(Dispatchers.IO) {
        userInformationDao.getUserInfoCount()
    }

    override suspend fun deleteUserInfo(id: String) = withContext(Dispatchers.IO) {
        userInformationDao.deleteUserInfo(id)
    }

    override suspend fun deleteAllUserInfo() = withContext(Dispatchers.IO) {
        userInformationDao.deleteAllUserInfo()
    }
}