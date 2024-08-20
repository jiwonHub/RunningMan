package com.cjwjsw.runningman.presentation.screen.login

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.data.preference.AppPreferenceManager
import com.cjwjsw.runningman.domain.usecase.FBStoreUserSignInCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appPreferenceManager: AppPreferenceManager
) : ViewModel()  {

    val myStateLiveData = MutableLiveData<LoginState2>(LoginState2.Uninitialized)
    val kakaoStateLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)
    val fbUsecase = FBStoreUserSignInCase()
    val userInfo = UserManager.getInstance();


    fun kakaoLogin(context: Context,auth:FirebaseAuth,onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
       kakaoStateLiveData.value = LoginState.Loading
        Log.d("LoginScreen","handleKakaoLoadingState")
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e("KakaoLoginwithOutApp", "카카오계정으로 로그인 실패", error)
                onFailure(error)
            } else if (token != null) {
                Log.e("KakaoLoginwithOutApp", "카카오계정으로 로그인 성공")
                viewModelScope.launch {
                    try {
                        Log.d("LoginViewModel","액세스 토큰 : ${token.accessToken}");
                        val user = withContext(Dispatchers.IO) { fetchUser(token, auth) }
                        user?.let {
                            val uidResult = fbUsecase.execute(auth, token.idToken.toString(), onSuccess = {
                                Log.d("LoginViewModel","파이어베이스 로그인 성공")
                            }, onFailure = {
                                Log.d("LoginViewModel","파이어베이스 로그인 실패")
                            })
                            uidResult.onSuccess { uid ->
                                UserManager.setUser(
                                    uid,
                                    user.kakaoAccount?.profile?.nickname.toString(),
                                    user.kakaoAccount?.email.toString(),
                                    user.kakaoAccount?.profile?.profileImageUrl.toString(),
                                    user.id.toString()
                                )
                            }.onFailure {
                                Log.e("FirebaseAuth", "signInWithCredential:failure", )
                            }
                        } ?: run {
                        }
                    }catch (e: Exception) {
                        Log.e(ContentValues.TAG, "Error during login process", e)
                    }
                    kakaoStateLiveData.value = LoginState.LoggedIn(token.idToken.toString())
                    onSuccess()
                    Log.d("LoginScreen", "handleKakaoLoggedInState")
                }
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
            firebaseUser?.let{ user ->
                myStateLiveData.value = LoginState2.Success.Registered(
                    userName = user.displayName ?: "익명",
                    profileImageUri = user.photoUrl,
                )
            }?: kotlin.run {
                myStateLiveData.value = LoginState2.Success.NotRegistered
            }
    }

    fun setKakaoUserInfo(firebaseUser: FirebaseUser?){
        firebaseUser?.let{ user ->
            Log.d("LoginScreen","handleRegisterState")
            kakaoStateLiveData.value = userInfo?.let {
                LoginState.Success.Registered(
                    token = user.uid,
                    userName = user.displayName ?: "익명",
                    profileImageUri = user.photoUrl.toString(),
                    email = user.email.toString(),
                    userNumber = it.userNumber
                )
            }
        }?: kotlin.run {
            Log.d("LoginScreen","handleNotRegisterState")
            kakaoStateLiveData.value = LoginState.Success.NotRegistered
        }
    }

    fun signOut() = viewModelScope.launch {
        withContext(Dispatchers.IO){
            appPreferenceManager.removeIdToken()
        }
        fetchData()
    }

    private suspend fun fetchUser(token: OAuthToken, auth: FirebaseAuth): User? {
        return suspendCancellableCoroutine { cont ->
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
                    cont.resume(null)
                } else if (user != null) {
                    Log.d("LoginViewModel",user.kakaoAccount?.profile.toString())
                    cont.resume(user)
                }
            }
        }
    }


}