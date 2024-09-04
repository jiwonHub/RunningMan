package com.cjwjsw.runningman.domain.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.cjwjsw.runningman.core.LocationTrackerManager
import com.cjwjsw.runningman.data.data_source.db.userInfo.UserInfoAppDatabase
import com.cjwjsw.runningman.data.data_source.db.walk.WalkAppDatabase
import com.cjwjsw.runningman.data.data_source.db.walk.WalkDao
import com.cjwjsw.runningman.data.data_source.weather.WeatherService
import com.cjwjsw.runningman.data.preference.AppPreferenceManager
import com.cjwjsw.runningman.data.repository.UserInfoRepositoryImpl
import com.cjwjsw.runningman.data.repository.WalkRepositoryImpl
import com.cjwjsw.runningman.data.repository.WeatherRepositoryImpl
import com.cjwjsw.runningman.domain.repository.UserInfoRepository
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

    @Binds
    @Singleton
    abstract fun bindUserInfoRepository(
        walkRepositoryImpl: UserInfoRepositoryImpl
    ): UserInfoRepository
}


@GlideModule
class GlideModule : AppGlideModule() {}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private const val WEATHER_BASE_URL = "https://api.open-meteo.com/"
    private const val PREFS_NAME = "WalkDataPrefs"

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

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
    fun provideAppDatabase(@ApplicationContext context: Context): WalkAppDatabase {
        return Room.databaseBuilder(
            context,
            WalkAppDatabase::class.java,
            "app_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideUserInfoAppDatabase(@ApplicationContext context: Context): UserInfoAppDatabase {
        return Room.databaseBuilder(
            context,
            UserInfoAppDatabase::class.java,
            "user_info_app_database"
        )
            .build()
    }

    // DAO 등록
    @Provides
    @Singleton
    fun provideWalkDao(appDatabase: WalkAppDatabase): WalkDao {
        return appDatabase.walkDao()
    }
}