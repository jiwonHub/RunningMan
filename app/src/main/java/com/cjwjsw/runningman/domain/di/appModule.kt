package com.cjwjsw.runningman.domain.di

import android.content.Context
import androidx.room.Room
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.cjwjsw.runningman.core.LocationTrackerManager
import com.cjwjsw.runningman.data.data_source.db.AppDatabase
import com.cjwjsw.runningman.data.data_source.db.WalkDao
import com.cjwjsw.runningman.data.data_source.weather.WeatherService
import com.cjwjsw.runningman.data.preference.AppPreferenceManager
import com.cjwjsw.runningman.data.repository.WalkRepositoryImpl
import com.cjwjsw.runningman.data.repository.WeatherRepositoryImpl
import com.cjwjsw.runningman.domain.repository.WalkRepository
import com.cjwjsw.runningman.domain.repository.WeatherRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Binds
    @Singleton
    abstract fun bindWalkRepository(
        walkRepositoryImpl: WalkRepositoryImpl
    ): WalkRepository
}


@GlideModule
class GlideModule : AppGlideModule() {}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
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
    fun provideFirebaseStorage() : FirebaseStorage{
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideWeatherService(retrofit: Retrofit): WeatherService {
        return retrofit.create(WeatherService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideAppPreferenceManager(@ApplicationContext context: Context): AppPreferenceManager {
        return AppPreferenceManager(context)
    }

    @Provides
    @Singleton
    fun provideLocationTrackerManager(): LocationTrackerManager {
        return LocationTrackerManager()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database" // 데이터베이스 이름
        ).build()
    }

    // DAO 등록
    @Provides
    @Singleton
    fun provideWalkDao(appDatabase: AppDatabase): WalkDao {
        return appDatabase.walkDao()
    }
}