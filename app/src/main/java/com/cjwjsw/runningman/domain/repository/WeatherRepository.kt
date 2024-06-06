package com.cjwjsw.runningman.domain.repository

import com.cjwjsw.runningman.domain.model.weather.CurrentWeatherModel
import com.cjwjsw.runningman.domain.model.weather.HourlyWeatherModel

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lng: Double): CurrentWeatherModel

    suspend fun getHourlyWeather(): HourlyWeatherModel
}