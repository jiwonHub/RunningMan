package com.cjwjsw.runningman.presentation.screen.feed

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityFeedMainBinding

class feedScreen: AppCompatActivity() {
    private lateinit var binding : ActivityFeedMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}