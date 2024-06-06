package com.cjwjsw.runningman.data.data_source.weather

import com.cjwjsw.runningman.data.dto.weather.WeatherDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String,
        @Query("hourly") hourly: String
    ): WeatherDTO
}