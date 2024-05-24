package com.cjwjsw.runningman.domain.factory

import android.content.ContextWrapper
import com.cjwjsw.runningman.data.preference.AppPreferenceManager
import com.cjwjsw.runningman.presentation.screen.login.LoginViewModel
import dagger.assisted.AssistedInject

class LoginViewModelFactoryImpl @AssistedInject constructor(
    private val appPreferenceManager: AppPreferenceManager
) : LoginViewModelFactory {
    override fun create(contextWrapper: ContextWrapper): LoginViewModel {
        return LoginViewModel(contextWrapper, appPreferenceManager)
    }
}