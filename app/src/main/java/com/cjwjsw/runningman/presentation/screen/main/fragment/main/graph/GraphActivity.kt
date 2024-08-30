package com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.ActivityGraphBinding
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.daily.DailyGraphFragment
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.month.MonthlyGraphFragment
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.weekly.WeeklyGraphFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GraphActivity : AppCompatActivity() {
    lateinit var binding: ActivityGraphBinding
    private var selectedButton: Button? = null
    private var selectedTextView: TextView? = null
    private val viewModel: GraphViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSelectedButton(binding.weekOptionButton)
        setSelectedTextView(binding.stepOption)
        viewModel.setSelectedOption(GraphViewModel.OPTION_STEPS)
        replaceFragment(WeeklyGraphFragment())

        binding.dailyOptionButton.setOnClickListener {
            setSelectedButton(it as Button)
            replaceFragment(DailyGraphFragment())
        }

        binding.weekOptionButton.setOnClickListener {
            setSelectedButton(it as Button)
            replaceFragment(WeeklyGraphFragment())
        }

        binding.monthOptionButton.setOnClickListener {
            setSelectedButton(it as Button)
            replaceFragment(MonthlyGraphFragment())
        }
        binding.stepOption.setOnClickListener {
            setSelectedTextView(it as TextView)
            viewModel.setSelectedOption(GraphViewModel.OPTION_STEPS)
        }

        binding.calorieOption.setOnClickListener {
            setSelectedTextView(it as TextView)
            viewModel.setSelectedOption(GraphViewModel.OPTION_CALORIES)
        }

        binding.timeOption.setOnClickListener {
            setSelectedTextView(it as TextView)
            viewModel.setSelectedOption(GraphViewModel.OPTION_TIME)
        }

        binding.distanceOption.setOnClickListener {
            setSelectedTextView(it as TextView)
            viewModel.setSelectedOption(GraphViewModel.OPTION_DISTANCE)
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.statisticsLayout, fragment)
        fragmentTransaction.commit()
    }

    private fun setSelectedButton(button: Button) {
        selectedButton?.setBackgroundResource(R.drawable.gray15_background) // 이전 선택된 버튼의 배경을 초기화
        button.setBackgroundResource(R.drawable.blue_background) // 클릭된 버튼의 배경을 파란색으로 설정
        selectedButton = button // 현재 선택된 버튼을 업데이트
    }

    private fun setSelectedTextView(textView: TextView) {
        selectedTextView?.apply {
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setBackgroundColor(Color.TRANSPARENT)
            paint.isUnderlineText = false
        }

        textView.apply {
            setTextColor(ContextCompat.getColor(context, R.color.blue))
            paint.isUnderlineText = true
        }

        selectedTextView = textView
    }
}