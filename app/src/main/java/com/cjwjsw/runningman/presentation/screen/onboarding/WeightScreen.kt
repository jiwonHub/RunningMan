package com.cjwjsw.runningman.presentation.screen.onboarding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityWeightBinding

class WeightScreen: AppCompatActivity() {

    private lateinit var binding: ActivityWeightBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gender = intent.getStringExtra("gender")
        Log.d("userdata",intent.getStringExtra("gender").toString())
        binding.nextButton.setOnClickListener {
            if (binding.weightEditText.text.isNotEmpty()) {
                val intent = Intent(this, HeightScreen::class.java)
                intent.putExtra("gender", gender)
                intent.putExtra("weight", binding.weightEditText.text.toString().toInt())
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "몸무게를 먼저 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}