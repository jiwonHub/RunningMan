package com.cjwjsw.runningman.presentation.screen.login
sealed class State{
        abstract val isLogin : Boolean
        object LoggedIn : State() {
            override val isLogin: Boolean = true
        }

        object LoggedOut : State() {
            override val isLogin: Boolean = false
        }

        object Loading : State() {
            override val isLogin: Boolean = false
        }

        object LoggedFailed : State(){
            override val isLogin: Boolean = false
        }
    }