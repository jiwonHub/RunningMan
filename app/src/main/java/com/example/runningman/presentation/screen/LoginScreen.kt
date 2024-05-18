package com.example.runningman.presentation.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.runningman.databinding.ActivityLoginBinding

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}