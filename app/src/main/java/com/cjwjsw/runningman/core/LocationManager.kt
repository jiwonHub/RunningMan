package com.cjwjsw.runningman.core

object LocationManager {
    var latitude: Double? = null
        private set
    var longitude: Double? = null
        private set

    fun updateLocation(lat: Double, lon: Double) {
        latitude = lat
        longitude = lon
    }

    fun hasLocation(): Boolean {
        return latitude != null && longitude != null
    }
}