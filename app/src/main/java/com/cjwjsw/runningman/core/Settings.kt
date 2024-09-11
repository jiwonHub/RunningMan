package com.cjwjsw.runningman.core

import android.content.Context
import android.content.SharedPreferences

object Settings {

    private const val PREFS_NAME = "steps_settings"
    private const val KEY_STEPS = "key_steps"
    private const val KEY_CALORIES = "key_calories"
    private const val KEY_DISTANCE = "key_distance"
    private const val KEY_TIME = "key_time"
    private const val KEY_WATER = "water"
    private const val KEY_TARGET_WATER = "target_water"

    private const val DEFAULT_STEPS = 10000
    private const val DEFAULT_CALORIES = 400
    private const val DEFAULT_DISTANCE = 7 // in km
    private const val DEFAULT_TIME = 60 // in minutes
    private const val DEFAULT_WATER = 300 // in ml
    private const val DEFAULT_TARGET_WATER = 1250 // in ml

    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 걸음 수 설정 및 가져오기
    var steps: Int
        get() = preferences.getInt(KEY_STEPS, DEFAULT_STEPS)
        set(value) = preferences.edit().putInt(KEY_STEPS, value).apply()

    // 칼로리 설정 및 가져오기
    var calories: Int
        get() = preferences.getInt(KEY_CALORIES, DEFAULT_CALORIES)
        set(value) = preferences.edit().putInt(KEY_CALORIES, value).apply()

    // 거리 설정 및 가져오기
    var distance: Int
        get() = preferences.getInt(KEY_DISTANCE, DEFAULT_DISTANCE)
        set(value) = preferences.edit().putInt(KEY_DISTANCE, value).apply()

    // 시간 설정 및 가져오기
    var time: Int
        get() = preferences.getInt(KEY_TIME, DEFAULT_TIME)
        set(value) = preferences.edit().putInt(KEY_TIME, value).apply()

    var water: Int
        get() = preferences.getInt(KEY_WATER, DEFAULT_WATER)
        set(value) = preferences.edit().putInt(KEY_WATER, value).apply()

    var targetWater: Int
        get() = preferences.getInt(KEY_TARGET_WATER, DEFAULT_TARGET_WATER)
        set(value) = preferences.edit().putInt(KEY_TARGET_WATER, value).apply()
}