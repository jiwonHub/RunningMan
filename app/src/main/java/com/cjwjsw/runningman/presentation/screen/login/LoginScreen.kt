package com.cjwjsw.runningman.presentation.screen.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.databinding.ActivityLoginBinding
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
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
               viewModel = LoginViewModel(this)
               viewModel.kakaoLogin()
        }

    }

}