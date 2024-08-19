package com.cjwjsw.runningman.domain.model

import com.google.firebase.Timestamp

data class FeedModel(
    val imageUrls: List<String>,
    val postId: String,
    val title: String,
    val content: String,
    val timestamp: Timestamp
)
