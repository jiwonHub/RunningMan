package com.cjwjsw.runningman.presentation.screen.login

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.domain.usecase.FBStoreUserSignInCase
import com.google.firebase.auth.FirebaseAuth
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel()  {
    val _stateValue: MutableLiveData<State> by lazy {
        MutableLiveData<State>()
    }
    val stateValue: LiveData<State> get() = _stateValue
    val fbUsecase = FBStoreUserSignInCase()

    fun kakaoLogin(context : Context,auth : FirebaseAuth){
        _stateValue.value = State.Loading

        //공용 콜백 선언
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(ContentValues.TAG, "카카오계정으로 로그인 실패", error)
                _stateValue.value = State.LoggedFailed
            } else if (token != null) {
                Log.i(ContentValues.TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                UserApiClient.instance.me { user, error ->
                    if(error != null){
                        Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        token.idToken?.let { fbUsecase.excute(auth, it) }
                    }
                }
                _stateValue.value = State.LoggedIn
            }
        }



        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e(ContentValues.TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(context, callback = callback )
                } else if (token != null) {
                    Log.i(ContentValues.TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    _stateValue.value = State.LoggedIn
                    UserApiClient.instance.me { user, error ->
                        if(error != null){
                            Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
                        }
                        else if (user != null) {
                            token.idToken?.let { fbUsecase.excute(auth, it) }
                        }
                    }
                }
                _stateValue.value = State.LoggedIn
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(context, callback = callback)
        }
    }

}