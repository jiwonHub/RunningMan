package com.cjwjsw.runningman.domain.model

data class UserModel (
    val nickName: String,
    val email: String,
    val profileUrl: String,
)

data class UserUid(
    val id : String
)