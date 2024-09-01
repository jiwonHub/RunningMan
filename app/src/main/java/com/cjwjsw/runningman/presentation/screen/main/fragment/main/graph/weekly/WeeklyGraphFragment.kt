package com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.weekly

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cjwjsw.runningman.core.StepsSettings
import com.cjwjsw.runningman.databinding.FragmentWeeklyStatisticsBinding
import com.cjwjsw.runningman.presentation.component.LabelUtils

import com.cjwjsw.runningman.presentation.component.StatisticsProgressBar
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.GraphViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class WeeklyGraphFragment : Fragment(), StatisticsProgressBar.BubbleListener {
    private var _binding: FragmentWeeklyStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GraphViewModel by activityViewModels()
    private var maxSteps = 0

    private lateinit var progressBars: List<StatisticsProgressBar>
    private lateinit var average: MutableList<Int>

    private lateinit var averageLabel: ImageView
    private lateinit var averageLabelTextView: TextView
    private lateinit var averageLabelLine: View
    private lateinit var label6k: ImageView
    private lateinit var textView6k: TextView
    private lateinit var dotLine6k: View

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentStartDate: LocalDate = LocalDate.now().with(DayOfWeek.MONDAY)
    @RequiresApi(Build.VERSION_CODES.O)
    private var currentEndDate: LocalDate = LocalDate.now().with(DayOfWeek.SUNDAY)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeeklyStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProgressBars()
        setupLabels()
        setupButtons()
        observeData()
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        binding.root.setOnClickListener {
            hideAllBubbles()
        }

        binding.timeLine.text = getCurrentWeekDateRange()
    }

    private fun observeData() {
        viewModel.selectedOption.observe(viewLifecycleOwner) { option ->
            // 옵션이 변경될 때마다 maxSteps를 업데이트
            updateMaxSteps(option, viewModel.weeklyProgressData.value ?: emptyList())
            // ProgressBars 업데이트
            updateProgressBars(viewModel.weeklyProgressData.value ?: emptyList())
        }
        viewModel.weeklyProgressData.observe(viewLifecycleOwner) { progressValues ->
            updateProgressBars(progressValues)
        }
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
        ).onEach { it.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onProgressBarTouched(it)
                true
            } else {
                false
            }
        }}
    }

    private fun setupLabels() {
        averageLabel = LabelUtils.createImageView(requireContext())
        averageLabelTextView = LabelUtils.createTextView(requireContext(), "평균")
        averageLabelLine = LabelUtils.createCustomView(requireContext())

        label6k = LabelUtils.create6kLabel(requireContext())
        textView6k = LabelUtils.create6kTextView(requireContext())
        dotLine6k = LabelUtils.create6kDotLine(requireContext())

        binding.weeklyStatisticsLayout.addView(averageLabel)
        binding.weeklyStatisticsLayout.addView(averageLabelTextView)
        binding.weeklyStatisticsLayout.addView(averageLabelLine)
        binding.weeklyStatisticsLayout.addView(label6k)
        binding.weeklyStatisticsLayout.addView(textView6k)
        binding.weeklyStatisticsLayout.addView(dotLine6k)

        updateLabels()
        LabelUtils.addStepLabels(binding.weeklyStatisticsLayout, maxSteps, average)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupButtons() {
        // 저번 주로 이동하는 버튼 설정
        binding.leftButton.setOnClickListener {
            moveToPreviousWeek()
        }

        // 다음 주로 이동하는 버튼 설정
        binding.rightButton.setOnClickListener {
            moveToNextWeek()
        }
    }

    private fun updateMaxSteps(option: String, progressValues: List<Int>) { // 그래프 최대 높이
        // 옵션에 따라 maxSteps 업데이트
        val optionMax = when (option) {
            GraphViewModel.OPTION_STEPS -> StepsSettings.steps
            GraphViewModel.OPTION_CALORIES -> StepsSettings.calories
            GraphViewModel.OPTION_DISTANCE -> StepsSettings.distance * 1000 // km to m
            GraphViewModel.OPTION_TIME -> StepsSettings.time * 60
            else -> 10000
        }
        // maxSteps는 데이터의 최대값과 설정된 옵션 값을 비교하여 동적으로 설정
        maxSteps = maxOf(optionMax, progressValues.maxOrNull() ?: 0, maxSteps)
        progressBars.forEach { it.setMaxSteps(maxSteps) } // 모든 ProgressBar에 최대 값 설정
        Log.d("maxSteps", maxSteps.toString())
        Log.d("maxSteps", progressValues.toString())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun moveToPreviousWeek() {
        // 현재 날짜 범위를 이전 주로 변경
        currentStartDate = currentStartDate.minusWeeks(1)
        currentEndDate = currentEndDate.minusWeeks(1)
        updateDateRangeText()
        fetchWeeklyData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun moveToNextWeek() {
        // 현재 날짜 범위를 다음 주로 변경
        currentStartDate = currentStartDate.plusWeeks(1)
        currentEndDate = currentEndDate.plusWeeks(1)
        updateDateRangeText()
        fetchWeeklyData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateRangeText() {
        val formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.getDefault())
        val startDateString = currentStartDate.format(formatter)
        val endDateString = currentEndDate.format(formatter)
        binding.timeLine.text = "$startDateString - $endDateString"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchWeeklyData() {
        // ViewModel을 사용해 현재 주의 데이터를 가져오는 로직을 구현
        viewModel.fetchWeeklyProgressData(currentStartDate, currentEndDate)
    }

    private fun updateProgressBars(progressValues: List<Int>) {
        progressValues.forEachIndexed { index, stepCount ->
            average[index] = stepCount
            val progressBar = progressBars.getOrNull(index)
            progressBar?.let {
                it.setProgressColor(stepCount > StepsSettings.steps) // 설정된 권장 걸음 수 이상일 때 색상 변경
                it.animateProgress(stepCount) // 애니메이션을 적용하여 ProgressBar 업데이트
                Log.d("WeeklyGraphFragment", "ProgressBar index $index updated with stepCount: $stepCount")
            }
        }
        updateLabels()
    }

    private fun updateLabels() {
        // 레이블 위치 업데이트
        LabelUtils.setAverageLabel(
            binding.weeklyStatisticsLayout, averageLabel, averageLabelTextView, averageLabelLine, average, maxSteps
        )
        LabelUtils.set6kLabel(
            binding.weeklyStatisticsLayout, label6k, textView6k, dotLine6k, maxSteps
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProgressBarTouched(touchedProgressBar: StatisticsProgressBar) {
        hideAllBubbles()
        touchedProgressBar.showCustomBubble()
    }

    private fun hideAllBubbles() {
        progressBars.forEach { it.hideCustomBubble() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentWeekDateRange(): String {
        val today = LocalDate.now()
        val monday = today.with(java.time.DayOfWeek.MONDAY)
        val sunday = today.with(java.time.DayOfWeek.SUNDAY)

        val formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.getDefault())
        val startDateString = monday.format(formatter)
        val endDateString = sunday.format(formatter)

        return "$startDateString - $endDateString"
    }

}