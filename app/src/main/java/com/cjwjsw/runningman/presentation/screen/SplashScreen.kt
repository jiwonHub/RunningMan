package com.cjwjsw.runningman.presentation.screen

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import com.cjwjsw.runningman.databinding.ActivitySplashBinding
import android.os.Handler
import android.util.Base64
import android.util.Log
import com.cjwjsw.runningman.presentation.screen.login.LoginScreen
import com.google.firebase.FirebaseApp
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    lateinit var binding : ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

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
                val text = binding.text

                // 텍스트가 투명해지는 애니메이션 적용
                ObjectAnimator.ofFloat(text, "alpha", 1f, 0f).apply {
                    duration = 500
                    start()
                }

                // 애니메이션 종료 후 0.5초 뒤에 다른 화면으로 전환
                Handler().postDelayed({
                    val intent = Intent(this@SplashScreen, LoginScreen::class.java)
                    startActivity(intent)
                    finish() // 현재 액티비티 종료
                }, 500) // 0.5초 후에 실행
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // Do nothing
            }
        })
        getDebugKeyHash(this)
    }
    fun getDebugKeyHash(context: Context): String? { // 디버그 키 해시 값 추출하기
        try {
            val packageName = context.packageName
            val packageInfo = context.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)

            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = String(Base64.encode(md.digest(), Base64.NO_WRAP))
                Log.d("Debug Key Hash", keyHash)
                return keyHash
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return null
    }
}