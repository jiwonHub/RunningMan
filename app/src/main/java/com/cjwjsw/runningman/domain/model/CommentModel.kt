package com.cjwjsw.runningman.domain.model

data class CommentModel(
    val comment: String = "",
    val userName: String = "",
    val timestamp: Long = 0,
    val profileUrl : String = "",
    val userUid : String = "",
    val userNumber : String = ""
)

