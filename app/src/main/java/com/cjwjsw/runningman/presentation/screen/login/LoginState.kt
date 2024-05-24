package com.cjwjsw.runningman.presentation.screen.login

import android.net.Uri
import androidx.annotation.StringRes

sealed class LoginState {
    data object Uninitialized: LoginState()

    data object Loading: LoginState()

    data class Login(
        val idToken: String
    ): LoginState()

    sealed class Success: LoginState() {
        data class Registered(
            val userId: String,
            val userName: String,
            val profileImageUri: Uri
        ): Success()

        data object NotRegistered: Success()
    }

    data class Error(
        @StringRes val messageId: Int,
        val e: Throwable
    ): LoginState()
}