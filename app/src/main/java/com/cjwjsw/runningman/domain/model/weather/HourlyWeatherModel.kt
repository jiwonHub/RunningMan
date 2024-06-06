package com.cjwjsw.runningman.domain.model.weather

data class HourlyWeatherModel(
    val apparent_temperature: List<Double>,
    val precipitation: List<Double>,
    val precipitation_probability: List<Int>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>,
    val wind_speed_10m: List<Double>
)
