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

    override suspend fun updateAge(id: String, age: Int) {
        userInformationDao.updateAge(id, age)
    }

    override suspend fun updateGender(id: String, gender: String) {
        userInformationDao.updateGender(id, gender)
    }

    override suspend fun updateHeight(id: String, height: Int) {
        userInformationDao.updateHeight(id, height)
    }

    override suspend fun updateWeight(id: String, weight: Int) {
        userInformationDao.updateWeight(id, weight)
    }
}