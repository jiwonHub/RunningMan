package com.cjwjsw.runningman.domain.di

import com.cjwjsw.runningman.data.data_source.weather.WeatherService
import com.cjwjsw.runningman.data.repository.WeatherRepositoryImpl
import com.cjwjsw.runningman.domain.repository.WeatherRepository
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"

    @Provides
    @Singleton
    fun proviedFireBase() : FirebaseStorage{
       return FirebaseStorage.getInstance()
    }



    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherService(retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }

    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO
}