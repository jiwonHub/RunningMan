package com.cjwjsw.runningman.domain.factory

import android.content.ContextWrapper
import com.cjwjsw.runningman.presentation.screen.login.LoginViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface LoginViewModelFactory {
    fun create(contextWrapper: ContextWrapper): LoginViewModel
}