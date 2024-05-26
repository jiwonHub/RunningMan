package com.cjwjsw.runningman.presentation.screen.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityHeightBinding

class HeightScreen: AppCompatActivity() {

    private lateinit var binding: ActivityHeightBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHeightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gender = intent.getStringExtra("gender")
        val weight = intent.getDoubleExtra("weight", 0.0)

        binding.nextButton.setOnClickListener {
            if (binding.heightEditText.text.isNotEmpty()) {
                val intent = Intent(this, AgeScreen::class.java)
                intent.putExtra("gender", gender)
                intent.putExtra("weight", weight)
                intent.putExtra("height", binding.heightEditText.text)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "키를 먼저 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}