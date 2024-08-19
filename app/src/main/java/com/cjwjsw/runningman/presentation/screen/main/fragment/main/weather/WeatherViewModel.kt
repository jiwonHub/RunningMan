package com.cjwjsw.runningman.presentation.screen.main.fragment.main.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.domain.model.weather.HourlyWeatherModel
import com.cjwjsw.runningman.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
): ViewModel() {
    private val _hourlyWeather = MutableLiveData<HourlyWeatherModel>()
    val hourlyWeather: LiveData<HourlyWeatherModel> get() = _hourlyWeather

    fun fetchHourlyWeather(lat: Double, lng: Double) {
        viewModelScope.launch {
            val hourlyWeather = weatherRepository.getHourlyWeather(lat, lng)
            _hourlyWeather.value = hourlyWeather
        }
    }
}