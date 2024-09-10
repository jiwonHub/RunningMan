package com.cjwjsw.runningman.presentation.screen.main.fragment.main.water

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityWaterBinding
import com.cjwjsw.runningman.presentation.component.LabelUtils
import com.cjwjsw.runningman.presentation.component.StatisticsProgressBar
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.water.watersetting.WaterSettingActivity
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class WaterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterBinding
    private val viewModel: WaterViewModel by viewModels()
    private lateinit var progressBars: List<StatisticsProgressBar>
    private lateinit var average: MutableList<Int>

    private lateinit var averageLabel: ImageView
    private lateinit var averageLabelTextView: TextView
    private lateinit var averageLabelLine: View
    private lateinit var label6k: ImageView
    private lateinit var textView6k: TextView
    private lateinit var dotLine6k: View

    private var currentStartDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
    private var currentEndDate: LocalDate = LocalDate.now().with(DayOfWeek.SUNDAY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        observeData()
        setupProgressBars()
        setupLabels()
        setupButtons()
    }

    private fun initViews() = with(binding) {
        waterText.text = viewModel.drinkingWater.value.toString()
        waterTargetText.text = "/ ${viewModel.targetWater.value}ml"
        timeLine.text = getCurrentWeekDateRange()
        drinkButton.setOnClickListener {
            viewModel.drinkWater()
        }
    }

    private fun observeData() {
        viewModel.drinkingWater.observe(this) { water ->
            binding.waterText.text = water.toString()
            updateProgressBar(water)
        }
        viewModel.weeklyWaters.observe(this@WaterActivity) { weeklyWaters ->
            updateProgressBars(weeklyWaters)
            updateStepTextWithTotal(weeklyWaters)
        }
        viewModel.targetWater.observe(this) { target ->
            binding.waterTargetText.text = "/ ${target}ml" // 목표 물 섭취량 업데이트
        }
    }


    private fun updateProgressBars(progressValues: List<Int>) {
        progressValues.forEachIndexed { index, drinkingWaters ->
            average[index] = drinkingWaters
            val progressBar = progressBars.getOrNull(index)

            progressBar?.let {
                it.setProgressColor(drinkingWaters >= (viewModel.targetWater.value?.toInt() ?: 0))
                it.animateProgress(drinkingWaters)
            }
        }
        updateLabels()
    }

    private fun setupProgressBars() {
        average = MutableList(7) { 0 }
        progressBars = listOf(
            binding.mondayProgressBar,
            binding.tuesdayProgressBar,
            binding.wednesdayProgressBar,
            binding.thursdayProgressBar,
            binding.fridayProgressBar,
            binding.saturdayProgressBar,
            binding.sundayProgressBar
        )
    }

    private fun setupLabels() = with(binding) {
        averageLabel = LabelUtils.createImageView(this@WaterActivity)
        averageLabelTextView = LabelUtils.createTextView(this@WaterActivity, "평균")
        averageLabelLine = LabelUtils.createCustomView(this@WaterActivity)

        label6k = LabelUtils.create6kLabel(this@WaterActivity)
        textView6k = LabelUtils.create6kTextView(this@WaterActivity)
        dotLine6k = LabelUtils.create6kDotLine(this@WaterActivity)

        waterLayout.addView(averageLabel)
        waterLayout.addView(averageLabelTextView)
        waterLayout.addView(averageLabelLine)
        waterLayout.addView(label6k)
        waterLayout.addView(textView6k)
        waterLayout.addView(dotLine6k)
        updateLabels()
        LabelUtils.addStepLabels(binding.waterLayout, viewModel.targetWater.value?.toInt() ?: 0, average)
    }

    private fun updateLabels() = with(binding) {
        // 레이블 위치 업데이트
        LabelUtils.setAverageLabel(
            waterLayout, averageLabel, averageLabelTextView, averageLabelLine, average, viewModel.targetWater.value?.toInt() ?: 0
        )

        LabelUtils.set6kLabel(
            waterLayout, label6k, textView6k, dotLine6k, viewModel.targetWater.value?.toInt() ?: 0
        )
    }

    private fun updateProgressBar(steps: Int) = with(binding) {
        val targetWater = viewModel.targetWater.value?.toInt() ?: 0
        val progress = (steps * 100) / targetWater
        waterProgressBar.setProgress(progress.coerceAtMost(100))
    }

    private fun setupButtons() = with(binding) {
        // 저번 주로 이동하는 버튼 설정
        leftButton.setOnClickListener {
            moveToPreviousWeek()
        }

        // 다음 주로 이동하는 버튼 설정
        rightButton.setOnClickListener {
            moveToNextWeek()
        }

        settingButton.setOnClickListener {
            val intent = Intent(this@WaterActivity, WaterSettingActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_SETTING)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SETTING && resultCode == RESULT_OK) {
            // 설정이 변경되었음을 감지하고 데이터를 다시 로드
            viewModel.fetchData()

            // 프로그레스 바 업데이트
            val drinkingWater = viewModel.drinkingWater.value ?: 0

            // 업데이트된 값을 기반으로 프로그레스 바를 새로고침
            updateProgressBar(drinkingWater)

            // 주간 프로그레스 바도 업데이트 필요 시 호출
            viewModel.weeklyWaters.value?.let { updateProgressBars(it) }
        }
    }

    private fun moveToPreviousWeek() {
        // 현재 날짜 범위를 이전 주로 변경
        currentStartDate = currentStartDate.minusWeeks(1)
        currentEndDate = currentEndDate.minusWeeks(1)
        updateDateRangeText()
        fetchWeeklyData()
    }

    private fun moveToNextWeek() {
        // 현재 날짜 범위를 다음 주로 변경
        currentStartDate = currentStartDate.plusWeeks(1)
        currentEndDate = currentEndDate.plusWeeks(1)
        updateDateRangeText()
        fetchWeeklyData()
    }

    private fun updateDateRangeText() {
        val formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.getDefault())
        val startDateString = currentStartDate.format(formatter)
        val endDateString = currentEndDate.format(formatter)

        binding.timeLine.text = "$startDateString - $endDateString"
    }

    private fun fetchWeeklyData() {
        val startDateString = currentStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val endDateString = currentEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        viewModel.getWaters(startDateString, endDateString)
    }

    private fun updateStepTextWithTotal(progressValues: List<Int>) {
        // progressValues의 총 합 계산
        val total = progressValues.sum()
        // 숫자 포맷: 천 단위마다 콤마 추가
        val formattedTotal = NumberFormat.getNumberInstance(Locale.getDefault()).format(total)

        // 포맷된 텍스트를 binding.stepText에 설정
        binding.waterTotalText.text = formattedTotal
    }


    private fun getCurrentWeekDateRange(): String {
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)
        val sunday = today.with(DayOfWeek.SUNDAY)

        val formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.getDefault())
        val startDateString = monday.format(formatter)
        val endDateString = sunday.format(formatter)

        return "$startDateString - $endDateString"
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveLiveDataToPreference()
    }

    companion object {
        private const val REQUEST_CODE_SETTING = 1001
    }

}