package com.cjwjsw.runningman.presentation.screen.onboarding.onboardingend

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.core.UserLoginFirst
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardingEndViewModel @Inject constructor() : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    fun saveUserData(userId: String?, gender: String, weight: Int, height: Int, age: Int,context : Context) {
        userId?.let {
            Log.d("OnBoardingEndViewModel",userId + gender + weight + height + age)
            val userRef = db.collection("user_info").document(userId)
            val userData = hashMapOf(
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

}