package com.cjwjsw.runningman.presentation.screen.login

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val viewModel: LoginViewModel by viewModels()

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
    }

    private val gsc by lazy { GoogleSignIn.getClient(this, gso) }

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.getResult(ApiException::class.java)?.let { account ->
                    viewModel.saveToken(account.idToken ?: throw Exception())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        KakaoSdk.init(this, "99180739a7bcf290c7df2a47e48e4767")

        setContentView(binding.root)
        initState()
    }

    private fun initState() {
        initViews()
        observeData()
    }

    private fun initViews() = with(binding) {
        viewModel.fetchData()
        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            // 애니메이션 시작
            binding.root.transitionToEnd()
        }

        binding.kakaoLogin.setOnClickListener {
            viewModel.kakaoLogin(this@LoginScreen)
        }

        binding.googleLogin.setOnClickListener {
            signInGoogle()
        }
    }

    private fun signInGoogle() {
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    private fun observeData() = viewModel.loginStateLiveData.observe(this) {
        when (it) {
            is LoginState.Error -> handleErrorState(it)
            is LoginState.Loading -> handleLoadingState()
            is LoginState.Login -> handleLoginState(it)
            is LoginState.Success -> handleSuccessState(it)
            else -> Unit
        }
    }

    private fun handleLoadingState() {

    }

    private fun handleErrorState(state: LoginState.Error) {

    }

    private fun handleSuccessState(state: LoginState.Success) {
        when (state) {
            is LoginState.Success.Registered -> {
                handleRegisteredState(state)
            }

            is LoginState.Success.NotRegistered -> {

            }
        }
    }

    private fun handleRegisteredState(state: LoginState.Success.Registered) = with(binding) {

    }

    private fun handleLoginState(state: LoginState.Login) {
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    viewModel.setUserInfo(user)
                } else {
                    firebaseAuth.signOut()
                    viewModel.setUserInfo(null)
                }
            }
    }

}