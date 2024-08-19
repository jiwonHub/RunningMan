package com.cjwjsw.runningman.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object WalkDataSingleton {
    private var _distance: Double = 0.0
    val distance: Double
        get() = _distance

    fun updateDistance(newDistance: Double) {
        _distance = newDistance
    }

    fun resetDistance() {
        _distance = 0.0
    }
}
