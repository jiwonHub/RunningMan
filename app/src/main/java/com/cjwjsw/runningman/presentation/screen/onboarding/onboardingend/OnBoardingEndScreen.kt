package com.cjwjsw.runningman.presentation.screen.onboarding.onboardingend

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.ActivityOnBoardingEndBinding
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingEndScreen: AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingEndBinding
    private val viewModel: OnBoardingEndViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingEndBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = UserManager.getInstance()
        val userId = user?.id

        val gender = intent.getStringExtra("gender")
        val weight = intent.getDoubleExtra("weight", 0.0)
        val height = intent.getDoubleExtra("height",0.0)
        val age = intent.getIntExtra("age", 0)

        userId?.let {
            viewModel.saveUserData(userId = userId, gender = gender ?: "", weight = weight, height = height, age = age)
        }
    }
}