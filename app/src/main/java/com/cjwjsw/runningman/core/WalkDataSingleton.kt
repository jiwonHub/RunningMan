package com.cjwjsw.runningman.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object WalkDataSingleton {
    private val _distance = MutableLiveData<Double>().apply { value = 0.0 }
    val distance: LiveData<Double> = _distance

    private val _stepCount = MutableLiveData<Int>().apply { value = 0 }
    val stepCount: LiveData<Int> = _stepCount

    private val _calorie = MutableLiveData<Double>().apply { value = 0.0 }
    val calorie: LiveData<Double> = _calorie

    private val _time = MutableLiveData<Long>().apply { value = 0L }
    val time: LiveData<Long> = _time

    fun updateDistance(newDistance: Double) {
        _distance.postValue(newDistance)
    }

    fun resetDistance() {
        _distance.postValue(0.0)
    }

    fun updateStepCount(newStepCount: Int) {
        _stepCount.postValue(newStepCount)
    }

    fun resetStepCount() {
        _stepCount.postValue(0)
    }

    fun updateCalorie(newCalorie: Double) {
        _calorie.postValue(newCalorie)
    }

    fun resetCalorie() {
        _calorie.postValue(0.0)
    }

    fun updateTime(newTime: Long) {
        _time.postValue(newTime)
    }

    fun resetTime() {
        _time.postValue(0L)
    }
}
