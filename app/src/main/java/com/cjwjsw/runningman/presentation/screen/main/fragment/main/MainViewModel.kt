package com.cjwjsw.runningman.presentation.screen.main.fragment.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.domain.model.weather.CurrentWeatherModel
import com.cjwjsw.runningman.domain.repository.WeatherRepository
import com.cjwjsw.runningman.service.PedometerService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel(){

    val stepCount: LiveData<Int> = PedometerService.stepCountLiveData
    val caloriesBurned: LiveData<Double> = PedometerService.caloriesBurnedLiveData
    val distanceWalked: LiveData<Double> = PedometerService.distanceWalkedLiveData
    val elapsedTime: LiveData<Long> = PedometerService.elapsedTimeLiveData

    private val _currentWeather = MutableLiveData<CurrentWeatherModel>()
    val currentWeather: LiveData<CurrentWeatherModel> get() = _currentWeather
    private val _latitude = MutableLiveData<Double>()
    private val _longitude = MutableLiveData<Double>()

    private val _weatherCodeText = MutableLiveData<String>()
    val weatherCodeText: LiveData<String> get() = _weatherCodeText
    fun setLocation(latitude: Double, longitude: Double) {
        _latitude.value = latitude
        _longitude.value = longitude
        fetchCurrentWeather()
    }

    fun fetchCurrentWeather() {
        viewModelScope.launch {
            val latitude = _latitude.value ?: 0.0
            val longitude = _longitude.value ?: 0.0

            val currentWeather = weatherRepository.getCurrentWeather(latitude, longitude)

            _currentWeather.value = currentWeather
            _weatherCodeText.value = setWeatherCodeText(currentWeather.weather_code)
            Log.d("currentWeather", currentWeather.toString())
        }
    }

    private fun setWeatherCodeText(weatherCode: Int): String {
        return when (weatherCode) {
            0 -> "맑음"
            1, 2, 3 -> "흐림"
            45, 48 -> "안개"
            51, 53, 55, 56, 57 -> "이슬비"
            61, 63, 65, 66, 67 -> "비"
            71, 73, 75, 77 -> "눈"
            80, 81, 82 -> "소나기"
            95, 96, 99 -> "뇌우"
            else -> "알 수 없는 날씨 코드"
        }
    }
}