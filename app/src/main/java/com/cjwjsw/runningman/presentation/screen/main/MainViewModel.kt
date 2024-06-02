package com.cjwjsw.runningman.presentation.screen.main

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

    private val _currentWeather = MutableLiveData<CurrentWeatherModel>()
    val currentWeather: LiveData<CurrentWeatherModel> get() = _currentWeather
    private val _latitude = MutableLiveData<Double>()
    private val _longitude = MutableLiveData<Double>()
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
        }
    }
}