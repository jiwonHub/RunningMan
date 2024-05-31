package com.cjwjsw.runningman.data.dto.weather

data class HourlyUnits(
    val apparent_temperature: String,
    val precipitation: String,
    val precipitation_probability: String,
    val temperature_2m: String,
    val time: String,
    val weather_code: String,
    val wind_speed_10m: String
)