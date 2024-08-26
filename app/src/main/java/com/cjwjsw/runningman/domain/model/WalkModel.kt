package com.cjwjsw.runningman.domain.model

data class WalkModel (
    val date: String,
    val distance: Double,
    val stepCount: Int,
    val calories: Double,
    val time: Long
)