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
import com.cjwjsw.runningman.databinding.FragmentMonthStatisticsBinding
import com.cjwjsw.runningman.presentation.component.LabelUtils
import com.cjwjsw.runningman.presentation.component.StatisticsDotLine
import com.cjwjsw.runningman.presentation.component.StatisticsProgressBar
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.GraphViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class MonthlyGraphFragment : Fragment(), StatisticsProgressBar.BubbleListener {
    private var _binding: FragmentMonthStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GraphViewModel by activityViewModels()
    private var maxSteps = 1000

    private lateinit var progressBars: List<StatisticsProgressBar>
    private lateinit var average: MutableList<Int>

    private lateinit var averageLabel: ImageView
    private lateinit var averageLabelTextView: TextView
    private lateinit var averageLabelLine: View
    private lateinit var label6k: ImageView
    private lateinit var textView6k: TextView
    private lateinit var dotLine6k: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMonthStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
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

        observeData()

        updateLabels()
        LabelUtils.addStepLabels(binding.monthStatisticsLayout, maxSteps, average)
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
        viewModel.monthProgressData.observe(viewLifecycleOwner) { progressValues ->
            updateProgressBar(progressValues)
        }
    }

    private fun updateLabels() {
        // 레이블 위치 업데이트
        LabelUtils.setAverageLabel(
            binding.monthStatisticsLayout, averageLabel, averageLabelTextView, averageLabelLine, average, maxSteps
        )
        LabelUtils.set6kLabel(
            binding.monthStatisticsLayout, label6k, textView6k, dotLine6k, maxSteps
        )
    }

    private fun updateProgressBar(progressValues: List<Int>) = with(binding) {
        progressValues.forEachIndexed { index, stepCount ->
            if (stepCount > maxSteps) {
                maxSteps = stepCount
                progressBars.forEach { it.setMaxSteps(maxSteps) }
            }

            average[index] = stepCount
            val progressBar = progressBars.getOrNull(index)
            progressBar?.let {
                it.setProgressColor(stepCount > 6000) // 값이 6000 이상일 때 색상 설정
                it.animateProgress(stepCount) // 애니메이션을 적용하여 ProgressBar 업데이트
                Log.d("DailyGraphFragment", "ProgressBar index $index updated with stepCount: $stepCount")
            }
        }
        updateLabels()
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