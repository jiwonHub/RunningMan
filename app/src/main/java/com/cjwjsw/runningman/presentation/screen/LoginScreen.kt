package com.cjwjsw.runningman.presentation.screen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityLoginBinding

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            // 애니메이션 시작
            binding.root.transitionToEnd()
        }
    }
}