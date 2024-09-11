package com.cjwjsw.runningman.presentation.screen.main.fragment.main.water

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.DataSingleton
import com.cjwjsw.runningman.core.Settings
import com.cjwjsw.runningman.domain.model.WaterModel
import com.cjwjsw.runningman.domain.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class WaterViewModel @Inject constructor(
    private val waterRepository: WaterRepository,
    private val appContext: Context,
    private val caretaker: WaterCaretaker
) : ViewModel() {

    private val _drinkingWater = MutableLiveData(DataSingleton.drinkingWater.value ?: 0)
    val drinkingWater: LiveData<Int> get() = _drinkingWater
    private val _targetWater = MutableLiveData(Settings.targetWater)
    val targetWater: LiveData<Int> get() = _targetWater
    var water = Settings.water // 마시는 물의 양
    private val _weeklyWaters = MutableLiveData<List<Int>>()
    val weeklyWaters: LiveData<List<Int>> get() = _weeklyWaters

    private val sharedPreferences = appContext.getSharedPreferences("WaterDataPrefs", Context.MODE_PRIVATE)

    init {
        getWaters(getStartOfWeek(), getEndOfWeek())
        restoreLiveDataFromPreference()
    }

    fun fetchData() {
        _drinkingWater.value = DataSingleton.drinkingWater.value ?: 0
        _targetWater.value = Settings.targetWater
        water = Settings.water
    }

    fun drinkWater() = viewModelScope.launch {
        val drinkingWaterAmount = drinkingWater.value ?: 0
        val currentWaterAmount = water
        val updateWater = drinkingWaterAmount + currentWaterAmount

        // 상태를 저장하기 전에 메멘토 생성 및 Caretaker에 저장
        val lastMemento = saveStateToMemento(drinkingWaterAmount)
        caretaker.addMemento(lastMemento)

        _drinkingWater.value = updateWater
        DataSingleton.updateDrinkingWater(updateWater)
    }

    // 날짜 범위를 받아 데이터를 가져오는 메소드 추가
    fun getWaters(startDate: String, endDate: String) = viewModelScope.launch {
        val waters = waterRepository.getWatersBetweenDates(startDate, endDate)

        val dailyWaters = MutableList(7) { 0 }

        // 데이터가 비어있으면 기본값으로 0을 설정하여 반환
        if (waters.isEmpty()) {
            withContext(Dispatchers.Main) {
                _weeklyWaters.value = dailyWaters
            }
            return@launch
        }

        waters.forEach { entry ->
            val date = LocalDate.parse(entry.date)
            val dayOfWeek = date.dayOfWeek.value
            val index = if (dayOfWeek == 7) 6 else dayOfWeek - 1

            dailyWaters[index] = entry.water
        }

        withContext(Dispatchers.Main) {
            _weeklyWaters.value = dailyWaters
        }
    }

    // 마지막 마신 물의 양을 취소하는 메서드
    fun cancelLastDrink() {
        val lastMemento = caretaker.getLastMemento()
        lastMemento?.let {
            restoreStateFromMemento(it)
            caretaker.removeLastMemento() // 복원 후 메멘토 제거
        }
    }

    // 메멘토를 생성하여 현재 상태를 저장
    private fun saveStateToMemento(waterAmount: Int): WaterModel {
        return WaterModel(waterAmount)
    }

    // 메멘토로부터 상태를 복원
    private fun restoreStateFromMemento(memento: WaterModel) {
        _drinkingWater.value = memento.drinkingWater
        DataSingleton.updateDrinkingWater(memento.drinkingWater)
    }

    private fun getStartOfWeek(): String {
        return LocalDate.now().with(DayOfWeek.SUNDAY).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    private fun getEndOfWeek(): String {
        return LocalDate.now().with(DayOfWeek.SATURDAY).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun saveLiveDataToPreference() {
        sharedPreferences.edit().apply {
            putInt("water", DataSingleton.drinkingWater.value?.toInt() ?: 0)
        }.apply()
    }

    private fun restoreLiveDataFromPreference() {
        DataSingleton.updateDrinkingWater(sharedPreferences.getInt("water", 0))
    }

}