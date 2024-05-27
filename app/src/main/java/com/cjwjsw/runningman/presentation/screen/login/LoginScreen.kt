package com.cjwjsw.runningman.presentation.screen.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.cjwjsw.runningman.databinding.ActivityLoginBinding
import com.cjwjsw.runningman.presentation.screen.onboarding.GenderScreen
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel : LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        KakaoSdk.init(this,"99180739a7bcf290c7df2a47e48e4767")

        setContentView(binding.root)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            // 애니메이션 시작
            binding.root.transitionToEnd()
        }


        binding.kakaoLogin.setOnClickListener {
            viewModel.kakaoLogin(this)
            viewModel.stateValue.observe(this,Observer{state ->
                val isLogin = state.isLogin
                if(isLogin){ //result 패턴, 수정해야함
                    val intent = Intent(this, GenderScreen::class.java)
                        startActivity(intent)
                }else{
                    Toast.makeText(this,"로그인 실패",Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

}