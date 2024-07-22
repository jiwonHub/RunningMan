package com.cjwjsw.runningman.presentation.screen.login

sealed class LoginState{
    data object Uninitialized : LoginState()
    data class LoggedIn(
        val token : String
    ) : LoginState()

    sealed class Success: LoginState() {

        data class Registered(
            val userName: String,
            val profileImageUri: String?,
            val email : String
        ): Success()
        data object NotRegistered: Success()

    }
        sealed class LoggedOut() : LoginState()

        data object Loading: LoginState()
        data class LoggedFailed(
            val error : Exception
        ) : LoginState()
    }