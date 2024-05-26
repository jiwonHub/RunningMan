package com.cjwjsw.runningman.presentation.screen.main

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.ActivityMainBinding
import com.kakao.sdk.user.UserApiClient


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val progressBar = binding.runningProgressBar
        val sundayProgressBar = binding.sundayProgressBar
        val mondayProgressBar = binding.mondayProgressBar
        val tuesdayProgressBar = binding.tuesdayProgressBar
        val wednesdayProgressBar = binding.wednesdayProgressBar
        val thursdayProgressBar = binding.thursdayProgressBar
        val fridayProgressBar = binding.fridayProgressBar
        val saturdayProgressBar = binding.saturdayProgressBar

        progressBar.setProgress(50)
        sundayProgressBar.setProgress(50)
        mondayProgressBar.setProgress(50)
        tuesdayProgressBar.setProgress(50)
        wednesdayProgressBar.setProgress(50)
        thursdayProgressBar.setProgress(50)
        fridayProgressBar.setProgress(50)
        saturdayProgressBar.setProgress(50)
    }
}