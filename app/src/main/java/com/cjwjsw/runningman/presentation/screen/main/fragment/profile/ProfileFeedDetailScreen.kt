package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivitiyFeedDetailBinding

class ProfileFeedDetailScreen : AppCompatActivity() {
    private lateinit var binding: ActivitiyFeedDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitiyFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}