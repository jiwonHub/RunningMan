package com.cjwjsw.runningman.domain.repository

interface UserDataRepository {
    suspend fun setUserData(id : String, nickname : String, weight : Int, )

}