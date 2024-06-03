package com.cjwjsw.runningman.core

import android.content.Context
import android.content.SharedPreferences

object UserLoginFirst {
    private const val PREFERENCES_NAME = "com.example.pocky.PREFERENCE_FILE_KEY"
    private const val FIRST_LOGIN_KEY = "first_login"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    fun isFirstLogin(context: Context): Boolean {
        val preferences = getPreferences(context)
        return preferences.getBoolean(FIRST_LOGIN_KEY, false)
    }

    fun setFirstLogin(context: Context, isFirstLogin: Boolean) {
        val preferences = getPreferences(context)
        val editor = preferences.edit()
        editor.putBoolean(FIRST_LOGIN_KEY, isFirstLogin)
        editor.apply()
    }
}