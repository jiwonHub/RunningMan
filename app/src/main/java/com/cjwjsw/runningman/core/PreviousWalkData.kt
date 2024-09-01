package com.cjwjsw.runningman.core

object PreviousWalkData {
    var previousDistance: Double = 0.0
    var previousStepCount: Int = 0
    var previousCalories: Double = 0.0
    var previousTime: Long = 0L

    fun updatePreviousData(currentDistance: Double, currentStepCount: Int, currentCalories: Double, currentTime: Long) {
        previousDistance = currentDistance
        previousStepCount = currentStepCount
        previousCalories = currentCalories
        previousTime = currentTime
    }
}