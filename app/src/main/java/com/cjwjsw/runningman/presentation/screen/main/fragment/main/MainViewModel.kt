package com.cjwjsw.runningman.presentation.screen.main.fragment.main

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.core.DataSingleton
import com.cjwjsw.runningman.domain.model.weather.CurrentWeatherModel
import com.cjwjsw.runningman.domain.repository.UserInfoRepository
import com.cjwjsw.runningman.domain.repository.WalkRepository
import com.cjwjsw.runningman.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val walkRepository: WalkRepository,
    private val userInfoRepository: UserInfoRepository,
    private val appContext: Context
): ViewModel() {

    val stepCount: LiveData<Int> = DataSingleton.stepCount
    val caloriesBurned: LiveData<Double> = DataSingleton.calorie
    val distanceWalked: LiveData<Double> = DataSingleton.distance
    val elapsedTime: LiveData<Long> = DataSingleton.time

    private val _currentWeather = MutableLiveData<CurrentWeatherModel>()
    val currentWeather: LiveData<CurrentWeatherModel> get() = _currentWeather

    private val _weeklyWalks = MutableLiveData<List<Int>>()
    val weeklyWalks: LiveData<List<Int>> get() = _weeklyWalks

    private val _weatherCodeText = MutableLiveData<String>()
    val weatherCodeText: LiveData<String> get() = _weatherCodeText

    private val sharedPreferences = appContext.getSharedPreferences("WalkDataPrefs", Context.MODE_PRIVATE)

    init {
        restoreLiveDataFromPreferences()
        observeStepCountChanges()
    }

    // 걸음 수가 변경될 때마다 호출되는 함수 설정
    private fun observeStepCountChanges() {
        stepCount.observeForever { steps ->
            updateCaloriesAndDistance(steps)
        }
    }

    private fun updateCaloriesAndDistance(steps: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 사용자 정보 가져오기
                val userInfo = userInfoRepository.getUserInfo(UserManager.getInstance()!!.idToken)

                // 사용자 정보로부터 필요한 데이터 추출
                val weight = userInfo.weight.toDouble() ?: 70.0 // kg, 기본값 사용
                val height = userInfo.height.toDouble() ?: 170.0 // cm, 기본값 사용
                val age = userInfo.age ?: 30 // 기본값 사용
                val gender = userInfo.gender ?: "man" // 기본값은 남성으로 설정

                // 성별에 따른 BMR(기초 대사량) 계산
                val bmr = calculateBMR(weight, height, age, gender)

                // 보폭 계산
                val strideLength = calculateStrideLength(height)

                // 걸음 수 기반 칼로리 소모량 및 거리 계산
                val caloriesBurned = calculateCalories(steps, bmr)
                val distanceWalked = calculateDistance(steps, strideLength)

                // 계산된 값 WalkDataSingleton에 업데이트
                withContext(Dispatchers.Main) {
                    DataSingleton.updateCalorie(caloriesBurned)
                    DataSingleton.updateDistance(distanceWalked)
                }

                Log.d("MainViewModel", "Calories: $caloriesBurned, Distance: $distanceWalked")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error calculating calories and distance", e)
            }
        }
    }

    // Mifflin-St Jeor 공식에 따른 BMR(기초 대사량) 계산
    private fun calculateBMR(weight: Double, height: Double, age: Int, gender: String): Double {
        return if (gender == "man") {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }
    }

    // 칼로리 계산 함수 - BMR을 기반으로 걸음 수에 따른 칼로리 계산
    private fun calculateCalories(stepCount: Int, bmr: Double): Double {
        // 예시로 BMR의 일부와 걸음 수를 곱하여 칼로리 계산 (기본적인 걸음당 칼로리 소모율 사용)
        val caloriesPerStep = bmr / 2000 * 0.04 // bmr을 이용해 개인 맞춤형 소모율
        return stepCount * caloriesPerStep
    }

    // 보폭 계산 함수 - 사용자 키와 보폭 비율을 사용하여 보폭 계산
    private fun calculateStrideLength(height: Double): Double {
        val strideRatio = 0.413 // 보폭 비율 예시
        return height * strideRatio // cm 단위로 보폭 계산
    }

    // 거리 계산 함수
    private fun calculateDistance(stepCount: Int, strideLength: Double): Double {
        return BigDecimal(stepCount * strideLength / 100000).setScale(2, RoundingMode.HALF_UP).toDouble() // km로 변환 후 반올림
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 주의 시작 날짜와 끝 날짜를 문자열로 포맷하여 가져옴
                val startDateString = getStartOfWeek() + "-00"
                val endDateString = getEndOfWeek() + "-23"

                // 지정된 날짜 범위 내의 모든 워킹 데이터를 가져옴
                val weeklyData = walkRepository.getWalksBetweenDates(startDateString, endDateString)

                // 일주일 동안의 요일별 걸음 수 합산을 위한 리스트 (일요일~토요일)
                val dailyTotals = MutableList(7) { 0 }

                // 데이터가 비어있으면 기본값으로 0을 설정하여 반환
                if (weeklyData.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _weeklyWalks.value = dailyTotals
                    }
                    return@launch
                }

                // 날짜별로 그룹화하여 요일별 걸음 수 합산
                val dailyDataMap = weeklyData.groupBy { it.date.substring(0, 10) }

                dailyDataMap.forEach { entry ->
                    val date = LocalDate.parse(entry.key)
                    val dayOfWeek = date.dayOfWeek.value
                    val index = if (dayOfWeek == 7) 6 else dayOfWeek - 1  // 일요일을 6번째 인덱스로 처리

                    // 각 날짜의 걸음 수를 합산
                    val totalSteps = entry.value.sumOf { it.stepCount }

                    // 합산된 걸음 수를 해당 요일의 인덱스에 추가
                    dailyTotals[index] = totalSteps
                }

                Log.d("fetchWeeklyWalks", dailyTotals.toString())

                // 합산된 데이터를 메인 스레드로 전환하여 LiveData에 업데이트
                withContext(Dispatchers.Main) {
                    _weeklyWalks.value = dailyTotals
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching weekly walks", e)
            }
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
            putFloat("distance", DataSingleton.distance.value?.toFloat() ?: 0.0f)
            putInt("stepCount", DataSingleton.stepCount.value ?: 0)
            putFloat("calorie", DataSingleton.calorie.value?.toFloat() ?: 0.0f)
            putLong("time", DataSingleton.time.value ?: 0L)
        }.apply()

        Log.d("MainViewModel", "Data saved to SharedPreferences")
    }

    private fun restoreLiveDataFromPreferences() {
        DataSingleton.updateDistance(sharedPreferences.getFloat("distance", 0.0f).toDouble())
        DataSingleton.updateStepCount(sharedPreferences.getInt("stepCount", 0))
        DataSingleton.updateCalorie(sharedPreferences.getFloat("calorie", 0.0f).toDouble())
        DataSingleton.updateTime(sharedPreferences.getLong("time", 0L))

        Log.d("MainViewModel", "Data restored from SharedPreferences")
    }

}
