package com.cjwjsw.runningman.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider

class FBStoreUserSignInCase {
    //카카오 idToken 가져오기
    fun excute(auth: FirebaseAuth, token: String) {
        val providerId = "oidc.kakaofornative"
        val credential = OAuthProvider.newCredentialBuilder(providerId)
            .setIdToken(token)
            .build()
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                // User is signed in
                Log.d("FirebaseAuth", "signInWithCredential:success")
                val user = authResult.user
                // Handle signed-in user
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.e("FirebaseAuth", "signInWithCredential:failure", e)

            }
    }
}
