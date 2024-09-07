package com.cjwjsw.runningman.presentation.screen.main.fragment.main.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.data.data_source.db.userInfo.UserInformationEntity
import com.cjwjsw.runningman.domain.repository.UserInfoRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository
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
        }
    }

    // 사용자 성별 수정 메서드
    fun updateGender(id: String, gender: String) {
        viewModelScope.launch {
            userInfoRepository.updateGender(id, gender)
        }
    }

    // 사용자 키 수정 메서드
    fun updateHeight(id: String, height: Int) {
        viewModelScope.launch {
            userInfoRepository.updateHeight(id, height)
        }
    }

    // 사용자 몸무게 수정 메서드
    fun updateWeight(id: String, weight: Int) {
        viewModelScope.launch {
            userInfoRepository.updateWeight(id, weight)
        }
    }

}