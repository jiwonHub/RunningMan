package com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.month

import android.annotation.SuppressLint
import android.graphics.PorterDuff
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.StepsSettings
import com.cjwjsw.runningman.databinding.FragmentMonthStatisticsBinding
import com.cjwjsw.runningman.presentation.component.LabelUtils
import com.cjwjsw.runningman.presentation.component.StatisticsDotLine
import com.cjwjsw.runningman.presentation.component.StatisticsProgressBar
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.GraphViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class MonthlyGraphFragment : Fragment(), StatisticsProgressBar.BubbleListener {
    private var _binding: FragmentMonthStatisticsBinding? = null
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

    private val daysInCurrentMonth: Int = getLastDayOfCurrentMonth()

    private val stepsMax = StepsSettings.steps / daysInCurrentMonth
    private val caloriesMax = StepsSettings.calories/ daysInCurrentMonth
    private val distanceMax = StepsSettings.distance / daysInCurrentMonth // km to m
    private val timeMax = StepsSettings.time * 60/ daysInCurrentMonth

    private var stepsMaxSteps = stepsMax
    private var caloriesMaxSteps = caloriesMax
    private var distanceMaxSteps = distanceMax
    private var timeMaxSteps = timeMax

    private var currentDate: LocalDate = LocalDate.now().withDayOfMonth(1) // 현재 날짜를 이번 달의 첫날로 설정

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMonthStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        setupProgressBars()
        setupLabels()
        observeData()
        initViews()
        setupButtons()
    }

    private fun setupProgressBars() = with(binding) {
        average = MutableList(31) { 0 }

        progressBars = listOf(
            progress1day, progress2day, progress3day, progress4day, progress5day, progress6day, progress7day,
            progress8day, progress9day, progress10day, progress11day, progress12day, progress13day, progress14day,
            progress15day, progress16day, progress17day, progress18day, progress19day, progress20day, progress21day,
            progress22day, progress23day, progress24day, progress25day, progress26day, progress27day, progress28day,
            progress29day, progress30day, progress31day
        ).onEach { it.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onProgressBarTouched(it)
                true
            } else {
                false
            }
        }}
    }

    private fun setupLabels() = with(binding) {
        averageLabel = LabelUtils.createImageView(requireContext())
        averageLabelTextView = LabelUtils.createTextView(requireContext(), "평균")
        averageLabelLine = LabelUtils.createCustomView(requireContext())

        label6k = LabelUtils.create6kLabel(requireContext())
        textView6k = LabelUtils.create6kTextView(requireContext())
        dotLine6k = LabelUtils.create6kDotLine(requireContext())

        monthStatisticsLayout.addView(averageLabel)
        monthStatisticsLayout.addView(averageLabelTextView)
        monthStatisticsLayout.addView(averageLabelLine)

        monthStatisticsLayout.addView(label6k)
        monthStatisticsLayout.addView(textView6k)
        monthStatisticsLayout.addView(dotLine6k)

        LabelUtils.addStepLabels(binding.monthStatisticsLayout, maxProgressBarHeight, average)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getLastDayOfCurrentMonth(): Int {
        // 이번 달의 마지막 날짜를 가져옴
        val lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())
        return lastDayOfMonth.dayOfMonth
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() = with(binding){
        root.setOnClickListener {
            hideAllBubbles()
        }

        val currentMonth = getCurrentMonth()
        month3day.text = "$currentMonth 3일"
        month9day.text = "$currentMonth 9일"
        month15day.text = "$currentMonth 15일"
        month21day.text = "$currentMonth 21일"
        month27day.text = "$currentMonth 27일"
        timeLine.text = currentMonth
    }

    private fun observeData() {
        viewModel.selectedOption.observe(viewLifecycleOwner) { option ->
            // 옵션이 변경될 때마다 maxSteps를 업데이트
            updateMaxSteps(option, viewModel.monthProgressData.value ?: emptyList())
            // ProgressBars 업데이트
            updateProgressBar(viewModel.monthProgressData.value ?: emptyList())
            // 총 합 텍스트 업데이트
            updateStepTextWithTotal(option, viewModel.monthProgressData.value ?: emptyList())
        }
        viewModel.monthProgressData.observe(viewLifecycleOwner) { progressValues ->
            updateMaxSteps(viewModel.selectedOption.value ?: GraphViewModel.OPTION_STEPS, progressValues)
            updateProgressBar(progressValues)
            updateStepTextWithTotal(viewModel.selectedOption.value ?: GraphViewModel.OPTION_STEPS, progressValues)
        }
    }

    private fun setupButtons() {
        // 저번 주로 이동하는 버튼 설정
        binding.leftButton.setOnClickListener {
            moveToPreviousMonth()
        }

        // 다음 주로 이동하는 버튼 설정
        binding.rightButton.setOnClickListener {
            moveToNextMonth()
        }
    }

    private fun updateLabels() {
        // 레이블 위치 업데이트
        LabelUtils.setAverageLabel(
            binding.monthStatisticsLayout, averageLabel, averageLabelTextView, averageLabelLine, average, maxProgressBarHeight
        )
        LabelUtils.set6kLabel(
            binding.monthStatisticsLayout, label6k, textView6k, dotLine6k, maxProgressBarHeight
        )
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

    private fun updateProgressBar(progressValues: List<Int>) = with(binding) {
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
    private fun moveToPreviousMonth() {
        // 현재 날짜를 이전 달의 첫날로 변경
        currentDate = currentDate.minusMonths(1).withDayOfMonth(1)
        updateDateText()
        fetchMonthlyData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun moveToNextMonth() {
        // 현재 날짜를 다음 달의 첫날로 변경
        currentDate = currentDate.plusMonths(1).withDayOfMonth(1)
        updateDateText()
        fetchMonthlyData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDateText() {
        val formatter = DateTimeFormatter.ofPattern("M월", Locale.getDefault()) // 날짜 형식 지정
        val formattedDate = currentDate.format(formatter)
        binding.timeLine.text = formattedDate // 날짜를 UI에 표시
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchMonthlyData() {
        // ViewModel을 사용해 현재 달의 데이터를 가져오는 로직을 구현
        viewModel.fetchMonthlyProgressData(currentDate.withDayOfMonth(1), currentDate.with(TemporalAdjusters.lastDayOfMonth()))
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
    private fun getCurrentMonth(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("M월", Locale.getDefault())
        return today.format(formatter)
    }
}