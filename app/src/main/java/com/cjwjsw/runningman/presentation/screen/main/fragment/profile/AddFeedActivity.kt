package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityAddFeedBinding

class AddFeedActivity: AppCompatActivity() {
    lateinit var binding : ActivityAddFeedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}