package com.cjwjsw.runningman.presentation.screen.login

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContextWrapper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.data.preference.AppPreferenceManager
import com.cjwjsw.runningman.domain.factory.LoginViewModelFactory
import com.cjwjsw.runningman.domain.model.UserModel
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @AssistedInject constructor(
    @Assisted private val contextWrapper: ContextWrapper,
    private val appPreferenceManager: AppPreferenceManager
) : ViewModel() {

    val loginStateLiveData = MutableLiveData<LoginState>(LoginState.Uninitialized)

    fun kakaoLogin() {

        //공용 콜백 선언
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(ContentValues.TAG, "카카오계정으로 로그인 실패", error)
            } else if (token != null) {
                Log.i(ContentValues.TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
                    } else if (user != null) {
                        //TODO 파베에 사용자 정보 저장
                        Log.i(
                            ContentValues.TAG, "사용자 정보 요청 성공" +
                                    "\n회원번호: ${user.id}" +
                                    "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                    "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                        )
                    }
                }
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(contextWrapper)) {
            UserApiClient.instance.loginWithKakaoTalk(contextWrapper) { token, error ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(contextWrapper, callback = callback)
                } else if (token != null) {
                    Log.i(ContentValues.TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(contextWrapper, callback = callback)
        }
    }

    fun saveToken(idToken: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            appPreferenceManager.putIdToken(idToken)
        }
        fetchData()
    }

    fun fetchData(): Job = viewModelScope.launch {
        loginStateLiveData.value = LoginState.Loading
        appPreferenceManager.getIdToken()?.let {
            loginStateLiveData.value = LoginState.Login(it)
        } ?: kotlin.run {
            loginStateLiveData.value = LoginState.Success.NotRegistered
        }
    }

    fun setUserInfo(firebaseUser: FirebaseUser?) = viewModelScope.launch {
        firebaseUser?.let { user ->
            appPreferenceManager.saveUserInfo(
                UserModel(
                    userId = user.uid,
                    profileImageUri = user.photoUrl,
                    userName = user.displayName ?: "익명"
                )
            )
        } ?: kotlin.run {
            loginStateLiveData.value = LoginState.Success.NotRegistered
        }
    }

    //TODO 파베 저장 코드 생성
    fun setDataToFB() {}

    companion object {
        fun provideFactory(
            assistedFactory: LoginViewModelFactory,
            contextWrapper: ContextWrapper
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(contextWrapper) as T
            }
        }
    }
}