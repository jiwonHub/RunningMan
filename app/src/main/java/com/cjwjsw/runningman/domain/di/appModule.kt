package com.cjwjsw.runningman.domain.di

import com.cjwjsw.runningman.domain.usecase.GoogleLoginUseCase
import com.cjwjsw.runningman.domain.usecase.KakaoLoginUseCase
import dagger.Module
import dagger.Provides
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
}