package com.cjwjsw.runningman.data.repository

import com.cjwjsw.runningman.data.data_source.weather.WeatherService
import com.cjwjsw.runningman.data.mapper.toCurrentWeatherModel
import com.cjwjsw.runningman.domain.model.weather.CurrentWeatherModel
import com.cjwjsw.runningman.domain.model.weather.HourlyWeatherModel
import com.cjwjsw.runningman.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val ioDispatcher: CoroutineDispatcher
): WeatherRepository {
    override suspend fun getCurrentWeather(lat: Double, lng: Double): CurrentWeatherModel = withContext(ioDispatcher) {
        val response = weatherService.getWeather(
            latitude = lat,
            longitude = lng,
            current = "temperature_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m",
            hourly = "temperature_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,wind_speed_10m"
        )

        val current = response.current
        return@withContext current.toCurrentWeatherModel()
    }

    override suspend fun getHourlyWeather(): HourlyWeatherModel = withContext(ioDispatcher){
        val response = weatherService.getWeather(
            latitude = 52.52,
            longitude = 13.41,
            current = "temperature_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m",
            hourly = "temperature_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,wind_speed_10m"
        )
        val hourly = response.hourly
        return@withContext HourlyWeatherModel(
            temperature_2m = hourly.temperature_2m,
            weather_code = hourly.weather_code,
            precipitation = hourly.precipitation,
            wind_speed_10m = hourly.wind_speed_10m,
            apparent_temperature = hourly.apparent_temperature,
            precipitation_probability = hourly.precipitation_probability
        )
    }
}