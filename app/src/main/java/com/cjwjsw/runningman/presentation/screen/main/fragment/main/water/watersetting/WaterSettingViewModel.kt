package com.cjwjsw.runningman.presentation.screen.main.fragment.main.water.watersetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterSettingViewModel @Inject constructor() : ViewModel() {

    private val _drinkingWater = MutableLiveData(Settings.water)
    val drinkingWater: LiveData<Int> get() = _drinkingWater

    private val _targetWater = MutableLiveData(Settings.targetWater)
    val targetWater: LiveData<Int> get() = _targetWater

    fun updateTargetWater(newTarget: Int) = viewModelScope.launch {
        Settings.targetWater = newTarget
        _targetWater.value = newTarget
    }

    fun updateDrinkingWater(newDrinking: Int) = viewModelScope.launch {
        Settings.water = newDrinking
        _drinkingWater.value = newDrinking
    }
}