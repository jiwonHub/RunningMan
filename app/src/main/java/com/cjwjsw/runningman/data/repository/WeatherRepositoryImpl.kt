package com.cjwjsw.runningman.data.repository

import com.cjwjsw.runningman.data.data_source.weather.WeatherService
import com.cjwjsw.runningman.data.mapper.toCurrentWeatherModel
import com.cjwjsw.runningman.domain.model.weather.CurrentWeatherModel
import com.cjwjsw.runningman.domain.model.weather.HourlyWeatherModel
import com.cjwjsw.runningman.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService,
    private val ioDispatcher: CoroutineDispatcher
): WeatherRepository {

    companion object {
        private const val CURRENT_WEATHER_PARAMS = "temperature_2m,apparent_temperature,precipitation,weather_code,wind_speed_10m"
        private const val HOURLY_WEATHER_PARAMS = "temperature_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,wind_speed_10m"
        private const val CACHE_DURATION = 60 * 60 * 1000 // 1시간 (밀리초)
    }

    private var currentWeatherCache: Pair<CurrentWeatherModel, Long>? = null
    private var hourlyWeatherCache: Pair<HourlyWeatherModel, Long>? = null
    private val cacheMutex = Mutex()

    override suspend fun getCurrentWeather(lat: Double, lng: Double): CurrentWeatherModel = withContext(ioDispatcher) {
        cacheMutex.withLock {
            val now = System.currentTimeMillis()
            val cached = currentWeatherCache
            if (cached != null && now - cached.second < CACHE_DURATION) {
                return@withContext cached.first
            }

            val response = weatherService.getWeather(
                latitude = lat,
                longitude = lng,
                current = CURRENT_WEATHER_PARAMS,
                hourly = HOURLY_WEATHER_PARAMS,
                hoursDays = 1,
                timezone = "Asia/Tokyo"
            )
            val currentWeatherModel = response.current.toCurrentWeatherModel()
            currentWeatherCache = Pair(currentWeatherModel, now)
            return@withContext currentWeatherModel
        }
    }

    override suspend fun getHourlyWeather(lat: Double, lng: Double): HourlyWeatherModel = withContext(ioDispatcher) {
        cacheMutex.withLock {
            val now = System.currentTimeMillis()
            val cached = hourlyWeatherCache
            if (cached != null && now - cached.second < CACHE_DURATION) {
                return@withContext cached.first
            }

            val response = weatherService.getWeather(
                latitude = lat,
                longitude = lng,
                current = CURRENT_WEATHER_PARAMS,
                hourly = HOURLY_WEATHER_PARAMS,
                hoursDays = 1,
                timezone = "Asia/Tokyo"
            )
            val hourlyWeatherModel = HourlyWeatherModel(
                temperature_2m = response.hourly.temperature_2m,
                weather_code = response.hourly.weather_code,
                precipitation = response.hourly.precipitation,
                wind_speed_10m = response.hourly.wind_speed_10m,
                apparent_temperature = response.hourly.apparent_temperature,
                precipitation_probability = response.hourly.precipitation_probability
            )
            hourlyWeatherCache = Pair(hourlyWeatherModel, now)
            return@withContext hourlyWeatherModel
        }
    }
}