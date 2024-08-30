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
import com.cjwjsw.runningman.databinding.FragmentMainBinding
import com.cjwjsw.runningman.presentation.component.MainRunningDailyProgressBar
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.GraphActivity
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.settings.SettingsActivity
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.weather.WeatherDetailActivity
import com.cjwjsw.runningman.service.PedometerService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: MainViewModel by viewModels()
    private var maxSteps = 1000

    private lateinit var progressBarMap: Map<String, MainRunningDailyProgressBar>
    private lateinit var todayDayOfWeek: String

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        viewModel.fetchWeeklyWalks()
    }

    private fun updateProgressBar(steps: Int) {
        val progress = (steps * 100) / maxSteps
        binding.runningProgressBar.setProgress(progress.coerceAtMost(100))
    }

    private fun updateProgressBarForDay(dayOfWeek: String, steps: Int) {
        progressBarMap[dayOfWeek]?.let {
            val progress = (steps * 100) / maxSteps
            it.setProgress(progress.coerceAtMost(100))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeData() {
        viewModel.stepCount.observe(viewLifecycleOwner) { stepCount ->
            binding.runningCountText.text = "$stepCount"
            updateProgressBarForDay(todayDayOfWeek, stepCount)
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

        viewModel.weeklyWalks.observe(viewLifecycleOwner) { walks ->
            walks?.forEach { walk ->
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val date = LocalDate.parse(walk.date, formatter)
                val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                updateProgressBarForDay(dayOfWeek, walk.stepCount)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        todayDayOfWeek = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

        progressBarMap = mapOf(
            "일요일" to binding.sundayProgressBar,
            "월요일" to binding.mondayProgressBar,
            "화요일" to binding.tuesdayProgressBar,
            "수요일" to binding.wednesdayProgressBar,
            "목요일" to binding.thursdayProgressBar,
            "금요일" to binding.fridayProgressBar,
            "토요일" to binding.saturdayProgressBar
        )

        progressBarMap.values.forEach { it.setProgress(0) }

        binding.setting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        binding.runningContainer.setOnClickListener {
            startActivity(Intent(requireContext(), GraphActivity::class.java))
        }

        binding.weatherLayout.setOnClickListener {
            startActivity(Intent(requireContext(), WeatherDetailActivity::class.java))
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.FOREGROUND_SERVICE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        if (permissionsNeeded.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsNeeded.toTypedArray())
        } else {
            startPedometerService()
            fetchLocation()
        }
    }

    private fun startPedometerService() {
        val serviceIntent = Intent(requireContext(), PedometerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requireActivity().startForegroundService(serviceIntent)
        } else {
            requireActivity().startService(serviceIntent)
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
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    viewModel.setLocation(it.latitude, it.longitude)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.saveLiveDataToPreferences()
        _binding = null
    }
}