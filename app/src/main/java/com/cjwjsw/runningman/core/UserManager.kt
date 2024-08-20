package com.cjwjsw.runningman.core

import com.cjwjsw.runningman.domain.model.UserModel


object UserManager {
    private var instance: UserModel? = null

    fun getInstance(): UserModel? {
        return instance
    }

    fun setUser(idToken: String, nickName: String, email: String, profileImageUrl: String,userNumber: String) {
        instance = UserModel(
            idToken = idToken,
            nickName = nickName,
            email = email,
            profileUrl = profileImageUrl,
            userNumber = userNumber
            )
    }

    fun clearUser() {
        instance = null
    }

}