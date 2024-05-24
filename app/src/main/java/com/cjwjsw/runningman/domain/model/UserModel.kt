package com.cjwjsw.runningman.domain.model

import android.net.Uri

data class UserModel(
    val userId: String,
    val userName: String,
    val profileImageUri: Uri?
)