package com.cjwjsw.runningman.presentation.screen.main

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.Manifest
import android.annotation.SuppressLint
import com.cjwjsw.runningman.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel : MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initFetchData()
        observeData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermissionAndFetchLocation()

        viewModel.currentWeather
    }

    private fun observeData() = viewModel.currentWeather.observe(this) { currentWeather ->
        currentWeather?.let {
            binding.weatherCode.text = "오늘 날씨: ${setWeatherCodeText(currentWeather.weather_code)}"
            binding.weatherTemp.text = "${currentWeather.temperature_2m}℃"
            binding.weatherPrecipitation.text = "${currentWeather.precipitation}%"
        }
    }

    private fun initFetchData() {
        viewModel.fetchCurrentWeather()
    }

    private fun initViews() {
        val progressBar = binding.runningProgressBar
        val sundayProgressBar = binding.sundayProgressBar
        val mondayProgressBar = binding.mondayProgressBar
        val tuesdayProgressBar = binding.tuesdayProgressBar
        val wednesdayProgressBar = binding.wednesdayProgressBar
        val thursdayProgressBar = binding.thursdayProgressBar
        val fridayProgressBar = binding.fridayProgressBar
        val saturdayProgressBar = binding.saturdayProgressBar
        val waterProgressBar = binding.waterProgressBar

        progressBar.setProgress(50)
        sundayProgressBar.setProgress(50)
        mondayProgressBar.setProgress(50)
        tuesdayProgressBar.setProgress(50)
        wednesdayProgressBar.setProgress(50)
        thursdayProgressBar.setProgress(50)
        fridayProgressBar.setProgress(50)
        saturdayProgressBar.setProgress(50)
        waterProgressBar.setProgress(50)
    }

    private fun setWeatherCodeText(weatherCode: Int): String {
        return when (weatherCode) {
            0 -> "맑음"
            1, 2, 3 -> "흐림"
            45, 48 -> "안개"
            51, 53, 55, 56, 57 -> "이슬비"
            61, 63, 65, 66, 67 -> "비"
            71, 73, 75, 77 -> "눈"
            80, 81, 82, 85, 86 -> "소나기"
            95, 96, 99-> "뇌우"
            else -> "알 수 없는 날씨 코드"
        }
    }

    private fun checkLocationPermissionAndFetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            fetchLocation()
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                fetchLocation()
            } else {
                // 권한이 거부된 경우 처리
            }
        }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우 예외 처리
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                viewModel.setLocation(latitude, longitude)
            }
        }
    }
}