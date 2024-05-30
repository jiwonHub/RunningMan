package com.cjwjsw.runningman.domain.usecase

import com.google.firebase.auth.FirebaseAuth

class FBStoreUserDataUseCase {

    fun excute(auth : FirebaseAuth,id : String, nick : String, email : String, profileImageUrl : String){
        auth.signInWithEmailAndPassword(email,id)
            .addOnCompleteListener { authResult ->
                if(authResult.isSuccessful){
                    return@addOnCompleteListener;
                }else{
                    auth.createUserWithEmailAndPassword(email,id)
                        .addOnCompleteListener {authResult ->
                            if(authResult.isSuccessful){
                                return@addOnCompleteListener
                            }else{
                                return@addOnCompleteListener
                            }
                        }
                    }
                }
            }

    //User모델에 사용자 정보 저장
//        UserManager.setUser(
//            id = id,
//            nickName = nick,
//            email= email,
//            profileImageUrl = profileImageUrl,
//        )
}