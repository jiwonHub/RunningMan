package com.cjwjsw.runningman.presentation.screen.onboarding.onboardingend

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.UserLoginFirst
import com.cjwjsw.runningman.data.data_source.db.userInfo.UserInformationEntity
import com.cjwjsw.runningman.domain.repository.UserInfoRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingEndViewModel @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    fun saveUserData(userId: String?, userImage: String, gender: String, weight: Int, height: Int, age: Int,context : Context) {
        userId?.let {
            Log.d("OnBoardingEndViewModel",userId + gender + weight + height + age)
            val userRef = db.collection("user_info").document(userId)
            val userData = hashMapOf(
                "userId" to userId,
                "userImage" to userImage,
                "gender" to gender,
                "weight" to weight,
                "height" to height,
                "age" to age
            )
            userRef.set(userData)
                .addOnSuccessListener {
                    Log.d("OnBoardingEndViewModel", "User data added successfully")
                    UserLoginFirst.setFirstLogin(context,true)

                }
                .addOnFailureListener { e ->
                    Log.e("OnBoardingEndViewModel", "Error adding user data", e)
                    UserLoginFirst.setFirstLogin(context,false)
                }
        }
    }

    fun saveUserInfo(userId: String, age: Int, gender: String, height: Int, weight: Int) = viewModelScope.launch {
        userId.let {
            val userInfo = UserInformationEntity(
                id = userId,
                age = age,
                gender = gender,
                height = height,
                weight = weight
            )
            userInfoRepository.insertUserInfo(userInfo)
        }
    }

}