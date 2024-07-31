package com.cjwjsw.runningman.presentation.screen.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.UserLoginFirst
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.ActivityLoginBinding
import com.cjwjsw.runningman.domain.usecase.FBStoreUserSignInCase
import com.cjwjsw.runningman.presentation.screen.main.MainActivity
import com.cjwjsw.runningman.presentation.screen.onboarding.GenderScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginScreen : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel : LoginViewModel by viewModels()
    val fbUsecase = FBStoreUserSignInCase()

    private val gso: GoogleSignInOptions by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }

    private val gsc by lazy { GoogleSignIn.getClient(this, gso) }

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                task.getResult(ApiException::class.java)?.let { account ->
                    viewModel.saveToken(account.idToken ?: throw Exception())
                    Log.d("please", account.displayName.toString())
                }
            } catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }

    fun observeData() = viewModel.myStateLiveData.observe(this) {
        when(it){
            is LoginState2.Loading -> handleLoadingState()
            is LoginState2.Success -> handleSuccessState(it)
            is LoginState2.Login -> handleLoginState(it)
            is LoginState2.Error -> handleErrorState(it)
            else -> Unit
        }
    }

    private fun handleLoadingState() {}
    private fun handleSuccessState(state: LoginState2.Success) = with(binding){
        when(state){
            is LoginState2.Success.Registered -> {
                handleRegisteredState(state)
                val intent = Intent(this@LoginScreen, MainActivity::class.java)
                startActivity(intent)
            }
            is LoginState2.Success.NotRegistered -> {
                Log.d("login error", "실패")
            }
        }
    }
    private fun handleRegisteredState(state: LoginState2.Success.Registered) = with(binding) {

    }
    private fun handleLoginState(state: LoginState2.Login) {
        val credential = GoogleAuthProvider.getCredential(state.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    val user = firebaseAuth.currentUser
                    viewModel.setUserInfo(user)
                }else{
                    firebaseAuth.signOut()
                    viewModel.setUserInfo(null)
                }
            }
    }
    private fun handleErrorState(state: LoginState2.Error) {}

    private fun signInGoogle(){
        val signInIntent = gsc.signInIntent
        loginLauncher.launch(signInIntent)
    }

    private fun handleKakaoLoadingState() {}

    private fun handleKakaoRegisteredState(
        loginState: LoginState.Success.Registered,
    ) {
        UserManager.setUser(
            idToken = loginState.token,
            nickName = loginState.userName ,
            profileImageUrl = loginState.profileImageUri.toString(),
            email = loginState.email)
    }


    private fun handleKakaoLogedinState(loginState: LoginState.LoggedIn, auth: FirebaseAuth,) {
        lifecycleScope.launch {
            fbUsecase.execute(auth,loginState.token,
                onSuccess = {
                    Log.d("LoginScreen","파이어베이스 로그인 성공${auth.currentUser?.uid}")
                    viewModel.setKakaoUserInfo(auth.currentUser)
                },
                onFailure = {
                    Log.d("LoginScreen","파이어베이스 로그인 실패${it.message}")
                })
        }
    }

    private fun handleKakaoLoginFailedState() {
        Log.d("LoginScreen","카카오 로그인 실패")
    }

    private fun handleKakaoSucessState(it: LoginState.Success) {
        when(it){
            is LoginState.Success.Registered ->{
                handleKakaoRegisteredState(it)
                Log.d("LoginScreen","handleKakaoSucessState")
            }
            is LoginState.Success.NotRegistered ->{
                Log.d("LoginScreen","유저 정보 등록 실패")
            }
        }
    }

    fun observeKakaoData(auth: FirebaseAuth) = viewModel.kakaoStateLiveData.observe(this) {
        when (it) {
            is LoginState.Loading -> handleKakaoLoadingState()
            is LoginState.LoggedIn -> handleKakaoLogedinState(it,auth)
            is LoginState.LoggedFailed -> handleKakaoLoginFailedState()
            is LoginState.Success -> handleKakaoSucessState(it)
            else -> Unit
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        KakaoSdk.init(this,"99180739a7bcf290c7df2a47e48e4767")
        auth = Firebase.auth
        val isFirstLogin = UserLoginFirst.isFirstLogin(this)
        Log.d("LoginScreen",UserLoginFirst.isFirstLogin(this).toString())

        setContentView(binding.root)
        observeData()
        observeKakaoData(auth)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener {
            // 애니메이션 시작
            binding.root.transitionToEnd()
        }


        binding.kakaoLogin.setOnClickListener {
            viewModel.kakaoLogin(this,auth,
                onSuccess = {
                    if(isFirstLogin){
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        val intent = Intent(this, GenderScreen::class.java)
                        startActivity(intent)
                    }
                },
                onFailure = {
                    Log.e("LoginScreen","로그인실패 ${it.message}")
                })
        }
        binding.googleLogin.setOnClickListener {
            signInGoogle()
        }

    }


}