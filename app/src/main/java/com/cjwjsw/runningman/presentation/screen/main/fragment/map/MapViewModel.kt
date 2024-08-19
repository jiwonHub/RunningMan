package com.cjwjsw.runningman.presentation.screen.main.fragment.map

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.LocationTrackerManager
import com.cjwjsw.runningman.core.WalkDataSingleton
import com.cjwjsw.runningman.data.data_source.db.DailyWalk
import com.cjwjsw.runningman.domain.repository.WalkRepository
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationTrackerManager: LocationTrackerManager,
    private val walkRepository: WalkRepository,
): ViewModel() {

    private val _path = MutableLiveData<List<LatLng>>()
    val path: LiveData<List<LatLng>> get() = _path

    private val _address = MutableLiveData<String>()
    val address: LiveData<String> get() = _address

    private var currentDate: String = getCurrentDate()

    init {
        viewModelScope.launch {
            val today = getCurrentDate()
            val walk = walkRepository.getWalkByDate(today)
            WalkDataSingleton.updateDistance(walk.distance)
        }
    }

    fun addLocation(latLng: LatLng, context: Context) {
        val today = getCurrentDate()
        if (currentDate != today) {
            resetData()
            currentDate = today
        }

        locationTrackerManager.addLocation(latLng)
        _path.value = locationTrackerManager.getPath()
        WalkDataSingleton.updateDistance(locationTrackerManager.getDistance())
        _address.value = getAddressFromLocation(latLng.latitude, latLng.longitude, context)
    }

    fun resetData() {
        locationTrackerManager.clearPath()
        WalkDataSingleton.resetDistance()
        _path.value = locationTrackerManager.getPath()
    }


    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double, context: Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val locality = address.locality ?: ""             // 예: 화성시
                val thoroughfare = address.thoroughfare ?: ""
                "$locality $thoroughfare"
            } else {
                "주소를 찾을 수 없습니다"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "주소를 찾을 수 없습니다"
        }
    }
}