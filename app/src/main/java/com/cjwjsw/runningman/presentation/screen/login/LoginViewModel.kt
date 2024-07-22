package com.cjwjsw.runningman.presentation.screen.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.data.preference.AppPreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager,
    @ApplicationContext private val context: Context
) : ViewModel()  {

    val myStateLiveData = MutableLiveData<LoginState2>(LoginState2.Uninitialized)
    val kakaoStateLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)


    fun kakaoLogin(context: Context, auth: FirebaseAuth) {
       kakaoStateLiveData.value = LoginState.Loading
        Log.d("LoginScreen","handleKakaoLoadingState")
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KakaoLoginwithOutApp", "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.e("KakaoLoginwithOutApp", "카카오계정으로 로그인 성공")
                kakaoStateLiveData.value = LoginState.LoggedIn(token.idToken.toString())
                Log.d("LoginScreen","handleKakaoLoggedInState")
            }

        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                handleKakaoLoginResult(token, error, callback, context)
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

    private fun handleKakaoLoginResult(token: OAuthToken?, error: Throwable?, callback: (OAuthToken?, Throwable?) -> Unit, context: Context) {
        if (error != null) {
            Log.e("KakaoLogin", "카카오톡으로 로그인 실패", error)

            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                return
            }
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        } else if (token != null) {
            Log.i("KakaoLogin", "카카오톡으로 로그인 성공 ${token.accessToken}")
            callback(token, null)
        }
    }

    private suspend fun fetchUser(token: OAuthToken, auth: FirebaseAuth): User? {
        return suspendCancellableCoroutine { cont ->
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
                    cont.resume(null)
                } else if (user != null) {
                    cont.resume(user)
                }
            }
        }
    }

    fun fetchData(): Job = viewModelScope.launch {
        myStateLiveData.value = LoginState2.Loading
        appPreferenceManager.getIdToken()?.let {
            myStateLiveData.value = LoginState2.Login(it)
        } ?: kotlin.run {
            myStateLiveData.value = LoginState2.Success.NotRegistered
        }
    }

    fun saveToken(idToken: String) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            appPreferenceManager.putIdToken(idToken)
            fetchData()
        }
    }

    fun setUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch{
        if(myStateLiveData.value != null){
            firebaseUser?.let{ user ->
                myStateLiveData.value = LoginState2.Success.Registered(
                    userName = user.displayName ?: "익명",
                    profileImageUri = user.photoUrl,
                )
            }?: kotlin.run {
                myStateLiveData.value = LoginState2.Success.NotRegistered
            }
        }else{
            firebaseUser?.let{ user ->
                Log.d("LoginScreen","handleRegisterState")
                kakaoStateLiveData.value = LoginState.Success.Registered(
                    userName = user.displayName ?: "익명",
                    profileImageUri = user.photoUrl.toString(),
                    email = user.email.toString()
                )
            }?: kotlin.run {
                Log.d("LoginScreen","handleNotRegisterState")
                kakaoStateLiveData.value = LoginState.Success.NotRegistered
            }
        }

    }

    fun signOut() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            appPreferenceManager.removeIdToken()
        }
        fetchData()
    }
}