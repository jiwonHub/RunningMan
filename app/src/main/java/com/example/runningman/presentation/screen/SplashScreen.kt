package com.example.runningman.presentation.screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.example.runningman.databinding.ActivitySplashBinding
import android.os.Handler

class SplashScreen : AppCompatActivity() {
    lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            // 애니메이션 시작
            binding.root.transitionToEnd()
        }
        binding.root.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                // Do nothing
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                // Do nothing
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                // 애니메이션 종료 후 0.8초 뒤에 다른 화면으로 전환
                Handler().postDelayed({
                    val intent = Intent(this@SplashScreen, LoginScreen::class.java)
                    startActivity(intent)
                    finish() // 현재 액티비티 종료
                }, 800) // 0.8초 후에 실행
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // Do nothing
            }
        })
    }
}