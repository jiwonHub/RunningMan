package com.cjwjsw.runningman.domain.di

import android.content.Context
import com.cjwjsw.runningman.data.preference.AppPreferenceManager
import com.cjwjsw.runningman.domain.usecase.GoogleLoginUseCase
import com.cjwjsw.runningman.domain.usecase.KakaoLoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.qualifiers.ApplicationContext
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
    fun provideAppPreferenceManager(@ApplicationContext context: Context): AppPreferenceManager {
        return AppPreferenceManager(context)
    }

}