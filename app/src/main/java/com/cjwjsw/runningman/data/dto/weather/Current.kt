package com.cjwjsw.runningman.data.dto.weather

data class Current(
    val apparent_temperature: Double,
    val interval: Int,
    val precipitation: Double,
    val temperature_2m: Double,
    val time: String,
    val weather_code: Int,
    val wind_speed_10m: Double
)