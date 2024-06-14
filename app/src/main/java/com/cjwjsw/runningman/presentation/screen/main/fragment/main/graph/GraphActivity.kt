package com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.ActivityGraphBinding
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.daily.DailyGraphFragment
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.month.MonthlyGraphFragment
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.graph.weekly.WeeklyGraphFragment

class GraphActivity : AppCompatActivity() {
    lateinit var binding: ActivityGraphBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGraphBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(WeeklyGraphFragment())

        binding.dailyOptionButton.setOnClickListener {
            replaceFragment(DailyGraphFragment())
        }

        binding.weekOptionButton.setOnClickListener {
            replaceFragment(WeeklyGraphFragment())
        }

        binding.monthOptionButton.setOnClickListener {
            replaceFragment(MonthlyGraphFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.statisticsLayout, fragment)
        fragmentTransaction.commit()
    }
}