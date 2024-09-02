package com.cjwjsw.runningman.presentation.screen.login

import android.net.Uri
import androidx.annotation.StringRes

sealed class LoginState2 {

    data object Uninitialized: LoginState2()

    data object Loading: LoginState2()

    data class Login(
        val idToken: String
    ): LoginState2()

    sealed class Success: LoginState2() {

        data class Registered(
            val userName: String,
            val profileImageUri: Uri?,
        ): Success()

        data object NotRegistered: Success()

    }

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable
    ): LoginState2()

}