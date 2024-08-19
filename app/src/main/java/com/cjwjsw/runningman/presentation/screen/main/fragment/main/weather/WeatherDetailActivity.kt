package com.cjwjsw.runningman.presentation.screen.main.fragment.main.weather

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.cjwjsw.runningman.core.LocationManager
import com.cjwjsw.runningman.databinding.ActivityWeatherDetailBinding
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.MainViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class WeatherDetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityWeatherDetailBinding
    private val viewModel: WeatherViewModel by viewModels()
    private val lat = LocationManager.latitude
    private val lng = LocationManager.longitude

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentDate = getCurrentDate()
        binding.dateText.text = currentDate

        // 날씨 데이터를 관찰하여 차트를 설정하는 메서드 호출
        observeHourlyWeather()

        // 날씨 데이터 요청
        viewModel.fetchHourlyWeather(lat ?: 0.0, lng ?: 0.0)
        Log.d("latlng", Pair(lat, lng).toString())
    }

    private fun observeHourlyWeather() {
        viewModel.hourlyWeather.observe(this) { hourlyWeather ->
            setupTemperatureChart(hourlyWeather.apparent_temperature)
            setupPrecipitationChart(hourlyWeather.precipitation_probability)
            setupWindSpeedChart(hourlyWeather.wind_speed_10m)
        }
    }

    private fun setupTemperatureChart(temperatureData: List<Double>) {
        val temperatureEntries = temperatureData.mapIndexed { index, temp ->
            Entry(index.toFloat(), temp.toFloat())
        }

        val temperatureDataSet = LineDataSet(temperatureEntries, "체감 온도").apply {
            color = Color.parseColor("#FFA500") // 주황색
            valueTextColor = Color.WHITE
            lineWidth = 2f
        }

        val lineData = LineData(temperatureDataSet)
        binding.lineChartTemperature.data = lineData

        configureChart(binding.lineChartTemperature)
    }

    private fun setupPrecipitationChart(precipitationData: List<Int>) {
        val precipitationEntries = precipitationData.mapIndexed { index, prob ->
            Entry(index.toFloat(), prob.toFloat())
        }

        val precipitationDataSet = LineDataSet(precipitationEntries, "강수 확률").apply {
            color = Color.parseColor("#1E90FF") // 파란색
            valueTextColor = Color.WHITE
            lineWidth = 2f
        }

        val lineData = LineData(precipitationDataSet)
        binding.lineChartPrecipitation.data = lineData

        configureChart(binding.lineChartPrecipitation)
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("MM월 dd일", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun setupWindSpeedChart(windSpeedData: List<Double>) {
        val windSpeedEntries = windSpeedData.mapIndexed { index, speed ->
            Entry(index.toFloat(), speed.toFloat())
        }

        val windSpeedDataSet = LineDataSet(windSpeedEntries, "풍속").apply {
            color = Color.parseColor("#00FF00") // 초록색
            valueTextColor = Color.WHITE
            lineWidth = 2f
        }

        val lineData = LineData(windSpeedDataSet)
        binding.lineChartWind.data = lineData

        configureChart(binding.lineChartWind)
    }

    private fun configureChart(lineChart: LineChart) {
        lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                granularity = 1f // 1시간 간격
                valueFormatter = XAxisValueFormatter()
            }
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false // 오른쪽 Y축 비활성화
            legend.textColor = Color.WHITE
            invalidate() // 차트 업데이트
        }
    }

    class XAxisValueFormatter : ValueFormatter() {
        private val hours = (0..23).map { "$it:00" } // 0시부터 23시까지의 시간 표시

        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return hours.getOrNull(value.toInt()) ?: value.toString()
        }
    }
}