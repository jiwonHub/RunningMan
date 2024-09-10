package com.cjwjsw.runningman.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object DataSingleton {
    private val _distance = MutableLiveData<Double>()
    val distance: LiveData<Double> = _distance

    private val _stepCount = MutableLiveData<Int>()
    val stepCount: LiveData<Int> = _stepCount

    private val _calorie = MutableLiveData<Double>()
    val calorie: LiveData<Double> = _calorie

    private val _time = MutableLiveData<Long>()
    val time: LiveData<Long> = _time

    private val _drinkingWater = MutableLiveData<Int>()
    val drinkingWater: LiveData<Int> = _drinkingWater // 마신 물

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

    fun updateDrinkingWater(newWater: Int) {
        _drinkingWater.postValue(newWater)
    }

    fun resetDrinkingWater() {
        _drinkingWater.postValue(0)
    }
}
