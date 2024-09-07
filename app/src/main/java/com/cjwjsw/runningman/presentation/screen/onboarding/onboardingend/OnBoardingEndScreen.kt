package com.cjwjsw.runningman.presentation.screen.onboarding.onboardingend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.core.UserLoginFirst
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.ActivityOnBoardingEndBinding
import com.cjwjsw.runningman.presentation.screen.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingEndScreen : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingEndBinding
    private val viewModel: OnBoardingEndViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = UserManager.getInstance()?.idToken

        Log.d("userdata", userId.toString())
        val gender = intent.getStringExtra("gender")
        val weight = intent.getIntExtra("weight", 0)
        val height = intent.getIntExtra("height", 0)
        val age = intent.getIntExtra("age", 0)
        val intent = Intent(this, MainActivity::class.java)

        binding.nextButton.setOnClickListener {
            userId?.let {
                viewModel.saveUserData(userId = userId, gender = gender ?: "", weight = weight, height = height, age = age, this)
                viewModel.saveUserInfo(userId = userId, gender = gender ?: "", weight = weight, height = height, age = age)
            }

            UserLoginFirst.setFirstLogin(this, true)

            startActivity(intent)
        }
    }
}