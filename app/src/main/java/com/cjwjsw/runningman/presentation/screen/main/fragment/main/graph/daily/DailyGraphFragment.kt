package com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.daily

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
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.FragmentDailyStatisticsBinding
import com.cjwjsw.runningman.databinding.FragmentWeeklyStatisticsBinding
import com.cjwjsw.runningman.presentation.component.LabelUtils
import com.cjwjsw.runningman.presentation.component.StatisticsDotLine
import com.cjwjsw.runningman.presentation.component.StatisticsProgressBar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class DailyGraphFragment : Fragment(), StatisticsProgressBar.BubbleListener {
    private var _binding: FragmentDailyStatisticsBinding? = null
    private val binding get() = _binding!!
    private var maxSteps = 1000

    private lateinit var progressBars: List<StatisticsProgressBar>
    private lateinit var average: MutableList<Int>

    private lateinit var averageLabel: ImageView
    private lateinit var averageLabelTextView: TextView
    private lateinit var averageLabelLine: View
    private lateinit var label6k: ImageView
    private lateinit var textView6k: TextView
    private lateinit var dotLine6k: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDailyStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        average = MutableList(24) { 0 }

        progressBars = listOf(
            oneProgress,
            twoProgress,
            threeProgress,
            fourProgress,
            fiveProgress,
            sixProgress,
            sevenProgress,
            eightProgress,
            nineProgress,
            tenProgress,
            elevenProgress,
            twelveProgress,
            thirteenProgress,
            fourteenProgress,
            fifteenProgress,
            sixteenProgress,
            seventeenProgress,
            eighteenProgress,
            nineteenProgress,
            twentyProgress,
            twentyOneProgress,
            twentyTwoProgress,
            twentyThreeProgress,
            twentyFourProgress
        ).apply {
            forEach { it.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onProgressBarTouched(it as StatisticsProgressBar)
                    true
                } else {
                    false
                }
            }}
        }

        averageLabel = LabelUtils.createImageView(requireContext())
        averageLabelTextView = LabelUtils.createTextView(requireContext(), "평균")
        averageLabelLine = LabelUtils.createCustomView(requireContext())

        label6k = LabelUtils.create6kLabel(requireContext())
        textView6k = LabelUtils.create6kTextView(requireContext())
        dotLine6k = LabelUtils.create6kDotLine(requireContext())

        dailyStatisticsLayout.addView(averageLabel)
        dailyStatisticsLayout.addView(averageLabelTextView)
        dailyStatisticsLayout.addView(averageLabelLine)

        dailyStatisticsLayout.addView(label6k)
        dailyStatisticsLayout.addView(textView6k)
        dailyStatisticsLayout.addView(dotLine6k)

        updateProgressBar(10000, 0)
        updateProgressBar(4000, 1)
        updateProgressBar(6000, 2)
        updateProgressBar(10000, 3)
        updateProgressBar(9000, 12)
        updateProgressBar(3000, 6)
        updateProgressBar(2600, 20)
        updateProgressBar(10000, 21)
        updateProgressBar(10000, 22)
        updateProgressBar(10000, 23)

        updateLabels()
        LabelUtils.addStepLabels(binding.dailyStatisticsLayout, maxSteps, average)

        binding.root.setOnClickListener {
            hideAllBubbles()
        }

        binding.timeLine.text = getTodayDate()
    }

    private fun updateLabels() {
        // 레이블 위치 업데이트
        LabelUtils.setAverageLabel(
            binding.dailyStatisticsLayout, averageLabel, averageLabelTextView, averageLabelLine, average, maxSteps
        )
        LabelUtils.set6kLabel(
            binding.dailyStatisticsLayout, label6k, textView6k, dotLine6k, maxSteps
        )
    }

    private fun updateProgressBar(stepCount: Int, index: Int) = with(binding) {
        if (stepCount > maxSteps) {
            maxSteps = stepCount
            progressBars.forEach { it.setMaxSteps(maxSteps) }
            updateLabels()
        }

        average[index] = stepCount
        val progressBar = progressBars[index]
        progressBar.setProgressColor(stepCount > 6000) // 색상 설정
        progressBar.animateProgress(stepCount)
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

    // 오늘의 날짜 계산
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTodayDate(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.getDefault())
        return today.format(formatter)
    }
}