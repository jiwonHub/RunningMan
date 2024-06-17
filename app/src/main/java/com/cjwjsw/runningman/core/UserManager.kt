package com.cjwjsw.runningman.core

import com.cjwjsw.runningman.domain.model.UserModel


object UserManager {
    private var instance: UserModel? = null

    fun getInstance(): UserModel? {
        return instance
    }

    fun setUser(id: String, nickName: String, email: String, profileImageUrl: String) {
        instance = UserModel(id, nickName, email, profileImageUrl)
    }

    fun clearUser() {
        instance = null
    }

    override fun toString(): String {
        val data : String = "id : " + instance?.id.toString() +  "email : " +instance?.email.toString() +  "nickname : " + instance?.nickName.toString() +  "profileUrl : " + instance?.profileUrl.toString()
        return data
    }
}