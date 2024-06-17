package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.ActivitiyFeedDetailBinding

class SocialDetailScreen: AppCompatActivity() {
    private lateinit var binding: ActivitiyFeedDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitiyFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadFeedImg()
        loadProfileImg()
        binding.backBtn.setOnClickListener {
            finish()
        }
    }


    fun loadFeedImg(){
        val imageUrl = intent.getStringExtra("URL")
        if (imageUrl != null) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.sun)
                .error(R.drawable.calories)
                .into(binding.feedImg)
        }
    }

    fun loadProfileImg(){
        val profileUrl = UserManager.getInstance()?.profileUrl
        if (profileUrl != null) {
            Glide.with(this)
                .load(profileUrl)
                .placeholder(R.drawable.sun)
                .error(R.drawable.calories)
                .into(binding.profileImg)
        }
    }
}