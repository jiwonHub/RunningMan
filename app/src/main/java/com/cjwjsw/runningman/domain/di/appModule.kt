package com.cjwjsw.runningman.domain.di

import com.cjwjsw.runningman.data.preference.AppPreferenceManager
import com.cjwjsw.runningman.domain.factory.LoginViewModelFactory
import com.cjwjsw.runningman.domain.factory.LoginViewModelFactoryImpl
import com.cjwjsw.runningman.domain.usecase.GoogleLoginUseCase
import com.cjwjsw.runningman.domain.usecase.KakaoLoginUseCase
import com.cjwjsw.runningman.presentation.screen.login.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.assisted.AssistedFactory
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideKakaoLoginUseCase() : KakaoLoginUseCase {
        return KakaoLoginUseCase()
    }

    @Provides
    @Singleton
    fun provideGoogleLoginUseCase() : GoogleLoginUseCase {
        return GoogleLoginUseCase()
    }

    @Provides
    @Singleton
    fun provideLoginViewModelFactory(
        appPreferenceManager: AppPreferenceManager
    ): LoginViewModelFactory {
        return LoginViewModelFactoryImpl(appPreferenceManager)
    }

}