package com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cjwjsw.runningman.databinding.FragmentMonthStatisticsBinding


class MonthlyGraphFragment : Fragment() {
    private var _binding: FragmentMonthStatisticsBinding? = null
    private val binding get() = _binding!!
    private var maxSteps = 1000

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentMonthStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateProgressBar(1000)
    }

    private fun updateProgressBar(stepCount: Int) {
        val progress = (stepCount * 100) / maxSteps
        val cappedProgress = progress.coerceAtMost(100)


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}