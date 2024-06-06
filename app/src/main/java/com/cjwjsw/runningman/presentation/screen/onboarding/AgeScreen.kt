package com.cjwjsw.runningman.presentation.screen.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityAgeBinding
import com.cjwjsw.runningman.presentation.screen.onboarding.onboardingend.OnBoardingEndScreen

class AgeScreen: AppCompatActivity() {

    private lateinit var binding: ActivityAgeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gender = intent.getStringExtra("gender")
        val weight = intent.getIntExtra("weight", 0)
        val height = intent.getIntExtra("height", 0)
        Log.d("userdata",intent.getIntExtra("weight",0).toString())

        binding.nextButton.setOnClickListener {
            if (binding.ageEditText.text.isNotEmpty()) {
                val intent = Intent(this, OnBoardingEndScreen::class.java)
                intent.putExtra("gender", gender)
                intent.putExtra("weight", weight)
                intent.putExtra("height", height)
                intent.putExtra("age", binding.ageEditText.text.toString().toInt())
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "몸무게를 먼저 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}