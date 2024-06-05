package com.cjwjsw.runningman.presentation.screen.feed

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.databinding.ActivityFeedMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class feedScreen: AppCompatActivity() {
    private lateinit var binding : ActivityFeedMainBinding
    private val viewModel : feedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedMainBinding.inflate(layoutInflater)

        viewModel.fetchImage()

        viewModel.imageUrl.observe(this) { url ->
            Log.d("url", url.toString())
            Glide.with(this)
                .load(url)
                .into(binding.exImg)
        }
    }
}