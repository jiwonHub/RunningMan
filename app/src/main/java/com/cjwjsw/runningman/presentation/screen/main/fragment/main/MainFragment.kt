package com.cjwjsw.runningman.presentation.screen.main.fragment.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cjwjsw.runningman.core.LocationManager
import com.cjwjsw.runningman.databinding.FragmentMainBinding
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.GraphActivity
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.settings.SettingsActivity
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.weather.WeatherDetailActivity
import com.cjwjsw.runningman.service.PedometerService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: MainViewModel by viewModels()
    private var maxSteps = 1000

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false).apply {
            this.viewModel = this@MainFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        checkAndRequestPermissions()

        binding.setting.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.runningContainer.setOnClickListener {
            val intent = Intent(requireContext(), GraphActivity::class.java)
            startActivity(intent)
        }

        binding.weatherLayout.setOnClickListener {
            val intent = Intent(requireContext(), WeatherDetailActivity::class.java)
            startActivity(intent)
        }

        viewModel.currentWeather
    }

    private fun startPedometerService() {
        val serviceIntent = Intent(requireContext(), PedometerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent)
        } else {
            requireActivity().startService(serviceIntent)
        }
    }

    private fun observeData() {
        viewModel.stepCount.observe(viewLifecycleOwner) { stepCount ->
            binding.runningCountText.text = "$stepCount"
            updateProgressBar(stepCount)
        }

        viewModel.caloriesBurned.observe(viewLifecycleOwner) { caloriesBurned ->
            binding.calorie.text = "%.2f".format(caloriesBurned)
        }

        viewModel.distanceWalked.observe(viewLifecycleOwner) { distanceWalked ->
            binding.distance.text = "%.2f".format(distanceWalked)
        }

        viewModel.elapsedTime.observe(viewLifecycleOwner) { elapsedTime ->
            val hours = elapsedTime / 3600
            val minutes = (elapsedTime % 3600) / 60
            binding.time.text = "%02d:%02d".format(hours, minutes)
        }
    }

    private fun updateProgressBar(stepCount: Int) {
        val progress = (stepCount * 100) / maxSteps
        val cappedProgress = progress.coerceAtMost(100)

        // Update the progress of runningProgressBar
        binding.runningProgressBar.setProgress(cappedProgress)
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

        progressBar.setProgress(0)
        sundayProgressBar.setProgress(50)
        mondayProgressBar.setProgress(50)
        tuesdayProgressBar.setProgress(50)
        wednesdayProgressBar.setProgress(50)
        thursdayProgressBar.setProgress(50)
        fridayProgressBar.setProgress(50)
        saturdayProgressBar.setProgress(50)
        waterProgressBar.setProgress(50)
    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 이상
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsNeeded.toTypedArray())
        } else {
            //startPedometerService()
            fetchLocation()
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            Log.d("MainFragment", "All required permissions granted")
            startPedometerService()
            fetchLocation()
        } else {
            Log.e("MainFragment", "Required permissions not granted")
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없을 경우 예외 처리
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latitude = it.latitude
                val longitude = it.longitude
                LocationManager.updateLocation(latitude, longitude)
                viewModel.setLocation(latitude, longitude)
            }
        }
    }

    fun updateMaxSteps(steps: Int) {
        maxSteps = steps
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}