package com.cjwjsw.runningman.presentation.screen.main.fragment.main.settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.data.data_source.db.userInfo.UserInformationEntity
import com.cjwjsw.runningman.domain.repository.UserInfoRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository,
    private val firebaseUserInfo : FirebaseFirestore
) : ViewModel() {

    private val _userInfo = MutableLiveData<UserInformationEntity>()
    val userInfo: LiveData<UserInformationEntity> get() = _userInfo

    // 사용자 정보 가져오기
    fun fetchUserInfo(userId: String) {
        viewModelScope.launch {
            _userInfo.value = userInfoRepository.getUserInfo(userId)
        }
    }

    // 사용자 나이 수정 메서드
    fun updateAge(id: String, age: Int) {
        viewModelScope.launch {
            userInfoRepository.updateAge(id, age)
            _userInfo.value = userInfoRepository.getUserInfo(id)
        }
    }

    fun updateAgeInFB(id : String, age : Int){
        viewModelScope.launch {
            firebaseUserInfo.collection("user_info")
                .document(userData?.idToken.toString())
                .update("age",age)
                .addOnSuccessListener{
                    Log.d(TAG,"유저 나이 파이어베이스에 수정 성공")
                }.addOnFailureListener {
                    Log.d(TAG,"유저 나이 파이어베이스에 수정 실패 : ${it.message.toString()}")
                }
        }
    }


    // 사용자 성별 수정 메서드
    fun updateGender(id: String, gender: String) {
        viewModelScope.launch {
            userInfoRepository.updateGender(id, gender)
            _userInfo.value = userInfoRepository.getUserInfo(id)
        }
    }

    fun updateGenderInFB(id : String, gender: String){
        viewModelScope.launch {
            firebaseUserInfo.collection("user_info")
                .document(userData?.idToken.toString())
                .update("gender",gender)
                .addOnSuccessListener{
                    Log.d(TAG,"유저 성별 파이어베이스에 수정 성공")
                }.addOnFailureListener {
                    Log.d(TAG,"유저 성별 파이어베이스에 수정 실패 : ${it.message.toString()}")
                }
        }
    }

    // 사용자 키 수정 메서드
    fun updateHeight(id: String, height: Int) {
        viewModelScope.launch {
            userInfoRepository.updateHeight(id, height)
            _userInfo.value = userInfoRepository.getUserInfo(id)
        }
    }

    fun updateHeightInFB(id : String, height: Int){
        viewModelScope.launch {
            firebaseUserInfo.collection("user_info")
                .document(userData?.idToken.toString())
                .update("height",height)
                .addOnSuccessListener{
                    Log.d(TAG,"유저 키 파이어베이스에 수정 성공")
                }.addOnFailureListener {
                    Log.d(TAG,"유저 키 파이어베이스에 수정 실패 : ${it.message.toString()}")
                }
        }
    }
    // 사용자 몸무게 수정 메서드
    fun updateWeight(id: String, weight: Int) {
        viewModelScope.launch {
            userInfoRepository.updateWeight(id, weight)
            _userInfo.value = userInfoRepository.getUserInfo(id)
        }
    }

    fun updateWeightInFB(id : String, weight: Int){
        viewModelScope.launch {
            firebaseUserInfo.collection("user_info")
                .document(id)
                .update("weight",weight)
                .addOnSuccessListener{
                    Log.d(TAG,"유저 몸무게 파이어베이스에 수정 성공")
                }.addOnFailureListener {
                    Log.d(TAG,"유저 몸무게 파이어베이스에 수정 실패 : ${it.message.toString()}")
                }
        }
    }

    companion object{
        // 유저 정보 싱글톤 가져오기
        private val userData = UserManager.getInstance()
        private val TAG = "SettingsViewModel"
    }
}