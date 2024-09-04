package com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.domain.repository.WalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

@HiltViewModel
class GraphViewModel @Inject constructor(
    private val walkRepository: WalkRepository
) : ViewModel() {

    companion object {
        const val OPTION_STEPS = "STEPS"
        const val OPTION_CALORIES = "CALORIES"
        const val OPTION_TIME = "TIME"
        const val OPTION_DISTANCE = "DISTANCE"
    }

    private val _selectedOption = MutableLiveData<String>()
    val selectedOption: LiveData<String> get() = _selectedOption

    private val _weeklyProgressData = MutableLiveData<List<Int>>()
    val weeklyProgressData: LiveData<List<Int>> get() = _weeklyProgressData

    private val _todayProgressData = MutableLiveData<List<Int>>()
    val todayProgressData: LiveData<List<Int>> get() = _todayProgressData

    private val _monthProgressData = MutableLiveData<List<Int>>()
    val monthProgressData: LiveData<List<Int>> get() = _monthProgressData

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedOption(option: String) {
        _selectedOption.value = option
        fetchTodayProgressData(getTodayDate())
        fetchWeeklyProgressData(getStartOfWeek(), getEndOfWeek())
        fetchMonthlyProgressData(getStartOfWeek(), getEndOfWeek())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchTodayProgressData(date: LocalDate) {
        val option = _selectedOption.value ?: OPTION_STEPS
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 오늘 날짜 설정
                val startDateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-00"
                val endDateString = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-23"

                // 오늘 날짜 범위의 데이터 조회
                val todayData = walkRepository.getWalksBetweenDates(startDateString, endDateString)

                // 시간별 데이터를 저장할 리스트 (0시부터 23시까지, 24개의 인덱스)
                val hourlyTotals = MutableList(24) { 0 }

                if (todayData.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _todayProgressData.value = hourlyTotals
                    }
                    return@launch
                }

                // 데이터를 시간별로 그룹화하여 합산
                val hourlyDataMap = todayData.groupBy { it.date.substring(11, 13).toInt() } // 시간 ("HH" 형식)으로 그룹화

                // 옵션에 따른 시간별 데이터 합산 리스트 생성
                hourlyDataMap.forEach { entry ->
                    val hour = entry.key // 시간 (0 ~ 23)

                    // 데이터 합산
                    val totalSteps = entry.value.sumOf { it.stepCount }
                    val totalCalories = entry.value.sumOf { it.calories.toInt() }
                    val totalTime = entry.value.sumOf { it.time.toInt() }
                    val totalDistance = entry.value.sumOf { it.distance.toInt() }

                    // 옵션에 따라 합산 값을 시간 인덱스에 맞게 저장
                    when (option) {
                        OPTION_STEPS -> hourlyTotals[hour] = totalSteps
                        OPTION_CALORIES -> hourlyTotals[hour] = totalCalories
                        OPTION_TIME -> hourlyTotals[hour] = totalTime
                        OPTION_DISTANCE -> hourlyTotals[hour] = totalDistance
                    }
                }

                Log.d("GraphViewModel", hourlyTotals.toString())
                withContext(Dispatchers.Main) {
                    _todayProgressData.value = hourlyTotals
                }
            } catch (e: Exception) {
                Log.e("GraphViewModel", "Error fetching today progress data", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchWeeklyProgressData(startDate: LocalDate, endDate: LocalDate) {
        val option = _selectedOption.value ?: OPTION_STEPS
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val startDateString = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-00"
                val endDateString = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-23"

                val weeklyData = walkRepository.getWalksBetweenDates(startDateString, endDateString)

                val dailyTotals = MutableList(7) { 0 }

                // 데이터가 비어있는 경우에도 기본값으로 0을 유지하도록 설정
                if (weeklyData.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _weeklyProgressData.value = dailyTotals
                    }
                    return@launch
                }

                val dailyDataMap = weeklyData.groupBy { it.date.substring(0, 10) }

                dailyDataMap.forEach { entry ->
                    val date = LocalDate.parse(entry.key)
                    val dayOfWeek = date.dayOfWeek.value
                    val index = if (dayOfWeek == 7) 6 else dayOfWeek - 1

                    val totalSteps = entry.value.sumOf { it.stepCount }
                    val totalCalories = entry.value.sumOf { it.calories.toInt() }
                    val totalTime = entry.value.sumOf { it.time.toInt() }
                    val totalDistance = entry.value.sumOf { it.distance.toInt() }

                    when (option) {
                        OPTION_STEPS -> dailyTotals[index] = totalSteps
                        OPTION_CALORIES -> dailyTotals[index] = totalCalories
                        OPTION_TIME -> dailyTotals[index] = totalTime
                        OPTION_DISTANCE -> dailyTotals[index] = totalDistance
                    }
                }

                Log.d("weeklyData", dailyTotals.toString())
                withContext(Dispatchers.Main) {
                    _weeklyProgressData.value = dailyTotals
                }
            } catch (e: Exception) {
                Log.e("GraphViewModel", "Error fetching weekly progress data", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchMonthlyProgressData(startDate: LocalDate, endDate: LocalDate) {
        val option = _selectedOption.value ?: OPTION_STEPS
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 현재 날짜에서 해당 월의 첫날과 마지막 날 설정
                val startDateString = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-00"
                val endDateString = endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "-23"

                // 해당 월의 데이터를 조회
                val monthlyData = walkRepository.getWalksBetweenDates(startDateString, endDateString)

                // 일별 데이터를 저장할 리스트 (1일부터 최대 31일까지, 31개의 인덱스)
                val monthTotals = MutableList(31) { 0 }

                if (monthlyData.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        _monthProgressData.value = monthTotals
                    }
                    return@launch
                }


                // 데이터를 날짜별로 그룹화하여 합산
                val dailyDataMap = monthlyData.groupBy { it.date.substring(0, 10) } // "yyyy-MM-dd" 형식으로 그룹화

                // 옵션에 따른 일별 데이터 합산 리스트 생성
                dailyDataMap.forEach { entry ->
                    // 날짜를 LocalDate로 변환하여 해당 일자 구하기
                    val date = LocalDate.parse(entry.key)
                    val dayOfMonth = date.dayOfMonth // 해당 월의 일자 (1 ~ 31)

                    // 데이터 합산
                    val totalSteps = entry.value.sumOf { it.stepCount }
                    val totalCalories = entry.value.sumOf { it.calories.toInt() }
                    val totalTime = entry.value.sumOf { it.time.toInt() }
                    val totalDistance = entry.value.sumOf { it.distance.toInt() }

                    // 옵션에 따라 합산 값을 해당 일자 인덱스에 맞게 저장 (1일은 index 0, 31일은 index 30)
                    when (option) {
                        OPTION_STEPS -> monthTotals[dayOfMonth - 1] = totalSteps
                        OPTION_CALORIES -> monthTotals[dayOfMonth - 1] = totalCalories
                        OPTION_TIME -> monthTotals[dayOfMonth - 1] = totalTime
                        OPTION_DISTANCE -> monthTotals[dayOfMonth - 1] = totalDistance
                    }
                }

                Log.d("GraphViewModel", monthTotals.toString())
                withContext(Dispatchers.Main) {
                    _monthProgressData.value = monthTotals
                }
            } catch (e: Exception) {
                Log.e("GraphViewModel", "Error fetching monthly progress data", e)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStartOfWeek(): LocalDate {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEndOfWeek(): LocalDate {
        return LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodayDate(): LocalDate {
        return LocalDate.now() // 현재 날짜를 반환
    }
}