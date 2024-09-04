package com.cjwjsw.runningman.presentation.screen.main.fragment.main

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.WalkDataSingleton
import com.cjwjsw.runningman.data.data_source.db.DailyWalk
import com.cjwjsw.runningman.domain.model.weather.CurrentWeatherModel
import com.cjwjsw.runningman.domain.repository.WalkRepository
import com.cjwjsw.runningman.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val walkRepository: WalkRepository,
    private val appContext: Context
): ViewModel() {

    val stepCount: LiveData<Int> = WalkDataSingleton.stepCount
    val caloriesBurned: LiveData<Double> = WalkDataSingleton.calorie
    val distanceWalked: LiveData<Double> = WalkDataSingleton.distance
    val elapsedTime: LiveData<Long> = WalkDataSingleton.time

    private val _currentWeather = MutableLiveData<CurrentWeatherModel>()
    val currentWeather: LiveData<CurrentWeatherModel> get() = _currentWeather

    private val _weeklyWalks = MutableLiveData<List<DailyWalk>>()
    val weeklyWalks: LiveData<List<DailyWalk>> get() = _weeklyWalks

    private val _weatherCodeText = MutableLiveData<String>()
    val weatherCodeText: LiveData<String> get() = _weatherCodeText

    private val sharedPreferences = appContext.getSharedPreferences("WalkDataPrefs", Context.MODE_PRIVATE)

    init {
        restoreLiveDataFromPreferences()
    }

    fun setLocation(latitude: Double, longitude: Double) {
        fetchCurrentWeather(latitude, longitude)
    }

    private fun fetchCurrentWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val currentWeather = weatherRepository.getCurrentWeather(latitude, longitude)
            _currentWeather.value = currentWeather
            _weatherCodeText.value = getWeatherCodeDescription(currentWeather.weather_code)
            Log.d("MainViewModel", "Current Weather: $currentWeather")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeeklyWalks() {
        viewModelScope.launch {
            val walks = walkRepository.getWalksBetweenDates(getStartOfWeek(), getEndOfWeek())
            _weeklyWalks.value = walks
        }
    }

    private fun getWeatherCodeDescription(weatherCode: Int): String {
        // Weather code를 설명 텍스트로 변환하는 로직 (예시)
        return when (weatherCode) {
            0, 1 -> "맑음"
            2, 3 -> "흐림"
            45, 48 -> "안개"
            51, 53, 55 -> "약한 비"
            61, 63, 65 -> "비"
            71, 73, 75 -> "눈"
            80, 81, 82 -> "소나기"
            else -> "알 수 없음"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStartOfWeek(): String {
        return LocalDate.now().with(DayOfWeek.SUNDAY).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEndOfWeek(): String {
        return LocalDate.now().with(DayOfWeek.SATURDAY).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun saveLiveDataToPreferences() {
        sharedPreferences.edit().apply {
            putFloat("distance", WalkDataSingleton.distance.value?.toFloat() ?: 0.0f)
            putInt("stepCount", WalkDataSingleton.stepCount.value ?: 0)
            putFloat("calorie", WalkDataSingleton.calorie.value?.toFloat() ?: 0.0f)
            putLong("time", WalkDataSingleton.time.value ?: 0L)
        }.apply()

        Log.d("MainViewModel", "Data saved to SharedPreferences")
    }

    private fun restoreLiveDataFromPreferences() {
        WalkDataSingleton.updateDistance(sharedPreferences.getFloat("distance", 0.0f).toDouble())
        WalkDataSingleton.updateStepCount(sharedPreferences.getInt("stepCount", 0))
        WalkDataSingleton.updateCalorie(sharedPreferences.getFloat("calorie", 0.0f).toDouble())
        WalkDataSingleton.updateTime(sharedPreferences.getLong("time", 0L))

        Log.d("MainViewModel", "Data restored from SharedPreferences")
    }

}
