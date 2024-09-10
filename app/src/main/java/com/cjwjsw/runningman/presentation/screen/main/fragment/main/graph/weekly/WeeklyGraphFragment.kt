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
import com.cjwjsw.runningman.core.Settings
import com.cjwjsw.runningman.databinding.FragmentWeeklyStatisticsBinding
import com.cjwjsw.runningman.presentation.component.LabelUtils

import com.cjwjsw.runningman.presentation.component.StatisticsProgressBar
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.GraphViewModel
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class WeeklyGraphFragment : Fragment(), StatisticsProgressBar.BubbleListener {
    private var _binding: FragmentWeeklyStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GraphViewModel by activityViewModels()
    private var maxProgressBarHeight = 0

    private lateinit var progressBars: List<StatisticsProgressBar>
    private lateinit var average: MutableList<Int>

    private lateinit var averageLabel: ImageView
    private lateinit var averageLabelTextView: TextView
    private lateinit var averageLabelLine: View
    private lateinit var label6k: ImageView
    private lateinit var textView6k: TextView
    private lateinit var dotLine6k: View

    private val stepsMax = Settings.steps
    private val caloriesMax = Settings.calories
    private val distanceMax = Settings.distance // km to m
    private val timeMax = Settings.time * 60

    private var stepsMaxSteps = stepsMax
    private var caloriesMaxSteps = caloriesMax
    private var distanceMaxSteps = distanceMax
    private var timeMaxSteps = timeMax

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
            // 총 합 텍스트 업데이트
            updateStepTextWithTotal(option, viewModel.weeklyProgressData.value ?: emptyList())
        }
        viewModel.weeklyProgressData.observe(viewLifecycleOwner) { progressValues ->
            updateMaxSteps(viewModel.selectedOption.value ?: GraphViewModel.OPTION_STEPS, progressValues)
            updateProgressBars(progressValues)
            // 총 합 텍스트 업데이트
            updateStepTextWithTotal(viewModel.selectedOption.value ?: GraphViewModel.OPTION_STEPS, progressValues)
        }
    }


    private fun updateStepTextWithTotal(option: String, progressValues: List<Int>) {
        // progressValues의 총 합 계산
        val total = progressValues.sum()
        // 숫자 포맷: 천 단위마다 콤마 추가
        val formattedTotal = NumberFormat.getNumberInstance(Locale.getDefault()).format(total)

        // 옵션에 따른 텍스트 포맷 설정
        val displayText = when (option) {
            GraphViewModel.OPTION_STEPS -> "$formattedTotal 걸음" // 걸음 수
            GraphViewModel.OPTION_CALORIES -> "$formattedTotal kcal" // 칼로리
            GraphViewModel.OPTION_DISTANCE -> {
                val distanceKm = total / 1000.0 // 미터를 km로 변환
                String.format("%.1f km", distanceKm) // 소수점 1자리로 표시
            }

            GraphViewModel.OPTION_TIME -> {
                // 분을 시, 분으로 변환
                val hours = total / 60
                val minutes = total % 60
                "${hours}시간 ${minutes}분"
            }

            else -> formattedTotal // 기본적으로는 숫자만 표시
        }

        // 포맷된 텍스트를 binding.stepText에 설정
        binding.stepText.text = displayText
        Log.d("WeeklyGraphFragment", "옵션: $option, 표시할 텍스트: $displayText")
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
        ).onEach {
            it.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onProgressBarTouched(it)
                    true
                } else {
                    false
                }
            }
        }
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
        LabelUtils.addStepLabels(binding.weeklyStatisticsLayout, maxProgressBarHeight, average)
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

    private fun updateMaxSteps(option: String, progressValues: List<Int>) {
        when (option) {
            GraphViewModel.OPTION_STEPS -> {
                // steps 옵션이 선택되었을 때만 stepsMaxSteps를 업데이트하고 maxSteps에 적용
                stepsMaxSteps = maxOf(stepsMax, progressValues.maxOrNull() ?: 0)
                maxProgressBarHeight = stepsMaxSteps
            }

            GraphViewModel.OPTION_CALORIES -> {
                // calories 옵션이 선택되었을 때만 caloriesMaxSteps를 업데이트하고 maxSteps에 적용
                caloriesMaxSteps = maxOf(caloriesMax, progressValues.maxOrNull() ?: 0)
                maxProgressBarHeight = caloriesMaxSteps
            }

            GraphViewModel.OPTION_DISTANCE -> {
                // distance 옵션이 선택되었을 때만 distanceMaxSteps를 업데이트하고 maxSteps에 적용
                distanceMaxSteps = maxOf(distanceMax, progressValues.maxOrNull() ?: 0)
                maxProgressBarHeight = distanceMaxSteps
            }

            GraphViewModel.OPTION_TIME -> {
                // time 옵션이 선택되었을 때만 timeMaxSteps를 업데이트하고 maxSteps에 적용
                timeMaxSteps = maxOf(timeMax, progressValues.maxOrNull() ?: 0)
                maxProgressBarHeight = timeMaxSteps
            }

            else -> {
                // 기본 옵션에 대한 처리
                maxProgressBarHeight = maxOf(10000, progressValues.maxOrNull() ?: 0)
            }
        }

        // 모든 ProgressBar에 새롭게 설정된 maxSteps를 적용
        progressBars.forEach { it.setMaxSteps(maxProgressBarHeight) }
        Log.d("maxSteps", "Option: $option, maxSteps set to: $maxProgressBarHeight")
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

            // 현재 선택된 옵션에 따라 기준 값을 설정
            val currentMax = when (viewModel.selectedOption.value) {
                GraphViewModel.OPTION_STEPS -> stepsMax
                GraphViewModel.OPTION_CALORIES -> caloriesMax
                GraphViewModel.OPTION_DISTANCE -> distanceMax
                GraphViewModel.OPTION_TIME -> timeMax
                else -> stepsMaxSteps // 기본값으로 stepsMaxSteps 사용
            }

            progressBar?.let {
                it.setProgressColor(stepCount >= currentMax) // 설정된 권장 걸음 수 이상일 때 색상 변경
                it.animateProgress(stepCount) // 애니메이션을 적용하여 ProgressBar 업데이트
                Log.d("WeeklyGraphFragment", "ProgressBar index $index updated with stepCount: $stepCount")
            }
        }
        updateLabels()
    }

    private fun updateLabels() {
        // 레이블 위치 업데이트
        LabelUtils.setAverageLabel(
            binding.weeklyStatisticsLayout, averageLabel, averageLabelTextView, averageLabelLine, average, maxProgressBarHeight
        )
        // 현재 옵션에 따른 초기 설정 값으로 6K 라벨 위치 설정
        val currentOptionMax = when (viewModel.selectedOption.value) {
            GraphViewModel.OPTION_STEPS -> stepsMax
            GraphViewModel.OPTION_CALORIES -> caloriesMax
            GraphViewModel.OPTION_DISTANCE -> distanceMax
            GraphViewModel.OPTION_TIME -> timeMax
            else -> stepsMax // 기본값으로 stepsMax 사용
        }

        LabelUtils.set6kLabel(
            binding.weeklyStatisticsLayout, label6k, textView6k, dotLine6k, currentOptionMax
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