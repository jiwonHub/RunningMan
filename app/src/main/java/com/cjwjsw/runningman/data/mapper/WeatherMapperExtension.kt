package com.cjwjsw.runningman.data.mapper

import com.cjwjsw.runningman.data.dto.weather.Current
import com.cjwjsw.runningman.data.dto.weather.Hourly
import com.cjwjsw.runningman.domain.model.weather.CurrentWeatherModel
import com.cjwjsw.runningman.domain.model.weather.HourlyWeatherModel

fun Current.toCurrentWeatherModel(): CurrentWeatherModel {
    return CurrentWeatherModel(
        apparent_temperature = this.apparent_temperature,
        weather_code = this.weather_code,
        precipitation = this.precipitation,
        temperature_2m = this.temperature_2m,
        wind_speed_10m = this.wind_speed_10m
    )
}

fun Hourly.toHourlyWeatherModel(): HourlyWeatherModel {
    return HourlyWeatherModel(
        precipitation = this.precipitation,
        weather_code = this.weather_code,
        apparent_temperature = this.apparent_temperature,
        temperature_2m = this.temperature_2m,
        wind_speed_10m = this.wind_speed_10m,
        precipitation_probability = this.precipitation_probability
    )
}