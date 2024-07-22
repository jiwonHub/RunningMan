package com.cjwjsw.runningman.core

import com.cjwjsw.runningman.domain.model.UserModel
import com.cjwjsw.runningman.domain.model.UserUid


object UserManager {
    private var instance: UserModel? = null
    private var instances : UserUid? = null

    fun getInstance(): UserModel? {
        return instance
    }

    fun getUidInstance():UserUid?{
        return instances
    }

    fun setUser(nickName: String, email: String, profileImageUrl: String) {
        instance = UserModel(nickName, email, profileImageUrl)
    }

    fun setUserUid(id : String){
        instances = UserUid(id)
    }

    fun clearUser() {
        instance = null
    }

}