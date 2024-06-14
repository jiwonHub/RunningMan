package com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.weekly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.FragmentWeeklyStatisticsBinding

import com.cjwjsw.runningman.presentation.component.StatisticsProgressBar


class WeeklyGraphFragment : Fragment() {
    private var _binding: FragmentWeeklyStatisticsBinding? = null
    private val binding get() = _binding!!
    private var maxSteps = 1000

    private lateinit var progressBars: List<StatisticsProgressBar>
    private lateinit var average: MutableList<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWeeklyStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        val label = createImageView()
        val textView = createTextView()
        val labelLine = createCustomView()

        binding.weeklyStatisticsLayout.addView(label)
        binding.weeklyStatisticsLayout.addView(textView)
        binding.weeklyStatisticsLayout.addView(labelLine)

        updateProgressBar(1000, 0)
        updateProgressBar(800, 1)
        updateProgressBar(600, 2)
        updateProgressBar(300, 3)

        setConstraintsForNewViews(binding.weeklyStatisticsLayout, label, textView, labelLine)
    }

    private fun createImageView(): ImageView { // 라벨 생성
        return ImageView(requireContext()).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.label)
            layoutParams = ConstraintLayout.LayoutParams(
                33.dpToPx(), // Convert dp to px
                33.dpToPx()  // Convert dp to px
            )
        }
    }

    private fun createTextView(): TextView { // 평균 텍스트 생성
        return TextView(requireContext()).apply {
            id = View.generateViewId()
            text = "평균"
            setTextSize(10f)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun createCustomView(): View { // 평균 라인 생성
        return View(requireContext()).apply {
            id = View.generateViewId()
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            layoutParams = ConstraintLayout.LayoutParams(
                0,
                3.dpToPx() // Convert dp to px
            )
        }
    }

    private fun setConstraintsForNewViews(
        constraintLayout: ConstraintLayout,
        imageView: ImageView,
        textView: TextView,
        customView: View
    ) {
        val constraintSet = ConstraintSet().apply {
            clone(constraintLayout)
            val nonZeroAverages = average.filter { it > 0 }
            val average = if (nonZeroAverages.isNotEmpty()) nonZeroAverages.average() else 0.0
            val height = 290 * (average / maxSteps)
            val averagePx = (height + 13).toInt().dpToPx()

            // ImageView constraints
            connect(imageView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(imageView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, averagePx)

            // TextView constraints
            connect(textView.id, ConstraintSet.START, imageView.id, ConstraintSet.START)
            connect(textView.id, ConstraintSet.END, imageView.id, ConstraintSet.END)
            connect(textView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.TOP)
            connect(textView.id, ConstraintSet.BOTTOM, imageView.id, ConstraintSet.BOTTOM)

            // Custom View constraints
            connect(customView.id, ConstraintSet.START, textView.id, ConstraintSet.END)
            connect(customView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            connect(customView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.TOP)
            connect(customView.id, ConstraintSet.BOTTOM, imageView.id, ConstraintSet.BOTTOM)
        }

        constraintSet.applyTo(constraintLayout)
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    private fun updateProgressBar(stepCount: Int, index: Int) = with(binding) {
        val progress = (stepCount * 100) / maxSteps
        val cappedProgress = progress.coerceAtMost(100)

        average[index] = stepCount
        progressBars[index].animateProgress(cappedProgress)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}