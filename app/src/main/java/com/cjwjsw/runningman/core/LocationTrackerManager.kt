package com.cjwjsw.runningman.core

import com.naver.maps.geometry.LatLng

class LocationTrackerManager {

    private val path = mutableListOf<LatLng>()
    private var distance: Double = 0.0

    fun addLocation(latLng: LatLng): Double {
        if (path.isNotEmpty()) {
            val lastLocation = path.last()
            distance += calculateDistance(lastLocation, latLng)
        }
        path.add(latLng)
        return distance
    }

    fun clearPath() {
        path.clear()
    }

    fun resetDistance() {
        distance = 0.0
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Float {
        val result = FloatArray(1)
        android.location.Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            result
        )
        return result[0]
    }

    fun getPath(): List<LatLng> = path

    fun getDistance(): Double = distance
}