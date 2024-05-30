package com.cjwjsw.runningman.domain.model.weather

data class CurrentWeatherModel (
    val apparent_temperature: Double,
    val precipitation: Double,
    val temperature_2m: Double,
    val weather_code: Int,
    val wind_speed_10m: Double
)