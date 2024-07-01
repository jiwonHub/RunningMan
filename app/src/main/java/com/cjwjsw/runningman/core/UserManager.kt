package com.cjwjsw.runningman.core

import com.cjwjsw.runningman.domain.model.UserModel


object UserManager {
    private var instance: UserModel? = null

    fun getInstance(): UserModel? {
        return instance
    }

    fun setUser(id: String, nickName: String, email: String, profileImageUrl: String, uid : String) {
        instance = UserModel(id, nickName, email, profileImageUrl, uid)
    }

    fun clearUser() {
        instance = null
    }
}