package com.cjwjsw.runningman.domain.model

data class CommentModel(
    val id : String,
    val comment : List<String>,
    val likedCount : Int
)
