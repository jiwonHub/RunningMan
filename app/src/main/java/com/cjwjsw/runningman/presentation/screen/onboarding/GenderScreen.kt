package com.cjwjsw.runningman.presentation.screen.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.ActivityGenderBinding

class GenderScreen : AppCompatActivity() {

    private lateinit var binding: ActivityGenderBinding
    private var isSelected: Boolean = false
    private lateinit var gender: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.menButton.setOnClickListener {
            binding.menButton.setBackgroundResource(R.drawable.blue_background)
            binding.womenButton.setBackgroundResource(R.drawable.gray15_background)
            isSelected = true
            gender = "man"
        }

        binding.womenButton.setOnClickListener {
            binding.menButton.setBackgroundResource(R.drawable.gray15_background)
            binding.womenButton.setBackgroundResource(R.drawable.pink_background)
            isSelected = true
            gender = "woman"
        }

        binding.nextButton.setOnClickListener {
            if (isSelected) {
                val intent = Intent(this, WeightScreen::class.java)
                intent.putExtra("gender", gender)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "성별을 먼저 선택해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

}