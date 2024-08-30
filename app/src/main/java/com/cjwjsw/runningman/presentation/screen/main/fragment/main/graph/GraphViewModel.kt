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

    private val _progressData = MutableLiveData<List<Int>>()
    val progressData: LiveData<List<Int>> get() = _progressData

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedOption(option: String) {
        _selectedOption.value = option
        fetchProgressData(option)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchProgressData(option: String) {

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 정확한 날짜 범위 설정
                val startDate = getStartOfWeek() + "-00"
                val endDate = getEndOfWeek() + "-23"

                // 정확한 날짜와 시간 비교를 위해 수정된 쿼리 사용
                val weeklyData = walkRepository.getWalksBetweenDates(startDate, endDate)

                if (weeklyData.isEmpty()) {
                    return@launch
                }

                // 데이터를 날짜별로 그룹화하여 합산
                val dailyDataMap = weeklyData.groupBy { it.date.substring(0, 10) } // "yyyy-MM-dd" 형식으로 그룹화

                // 옵션에 따른 합산 리스트 생성
                val dailyTotals = dailyDataMap.map { entry ->
                    val totalSteps = entry.value.sumOf { it.stepCount }
                    val totalCalories = entry.value.sumOf { it.calories.toInt() }
                    val totalTime = entry.value.sumOf { (it.time / 60).toInt() }
                    val totalDistance = entry.value.sumOf { it.distance.toInt() }

                    when (option) {
                        OPTION_STEPS -> totalSteps
                        OPTION_CALORIES -> totalCalories
                        OPTION_TIME -> totalTime
                        OPTION_DISTANCE -> totalDistance
                        else -> 0
                    }
                }
                Log.d("GraphViewModel", dailyTotals.toString())
                withContext(Dispatchers.Main) {
                    _progressData.value = dailyTotals
                }
            } catch (e: Exception) {
                Log.e("GraphViewModel", "Error fetching progress data", e)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStartOfWeek(): String {
        val today = LocalDate.now()
        // 오늘이 일요일이거나 이전 일요일을 찾음
        val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        return startOfWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getEndOfWeek(): String {
        val today = LocalDate.now()
        // 오늘이 토요일이거나 다음 토요일을 찾음
        val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
        return endOfWeek.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}