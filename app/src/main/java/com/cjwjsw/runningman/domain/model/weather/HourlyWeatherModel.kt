package com.cjwjsw.runningman.domain.model.weather

data class HourlyWeatherModel(
    val apparent_temperature: List<Double>,
    val precipitation: List<Double>,
    val precipitation_probability: List<Int>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>,
    val wind_speed_10m: List<Double>
) {
    override fun toString(): String {
        return "HourlyWeatherModel(" +
                "apparentTemperature=$apparent_temperature, " +
                "precipitation=$precipitation, " +
                "precipitationProbability=$precipitation_probability, " +
                "temperature2m=$temperature_2m, " +
                "weatherCode=$weather_code, " +
                "windSpeed10m=$wind_speed_10m)"
    }
}
