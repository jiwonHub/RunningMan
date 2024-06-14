package com.cjwjsw.runningman.domain.usecase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FBStoreUserSignInCase @Inject constructor(@ApplicationContext private val context: Context) {

    //카카오 idToken 가져오기
    suspend fun execute(auth: FirebaseAuth, token: String): Result<String> {
        return suspendCancellableCoroutine { cont ->
            val providerId = "oidc.kakaofornative"
            val credential = OAuthProvider.newCredentialBuilder(providerId)
                .setIdToken(token)
                .build()
            auth.signInWithCredential(credential)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        Log.d("FirebaseAuth", "signInWithCredential:success${user.uid}")
                        cont.resume(Result.success(user.uid))
                    } else {
                        cont.resume(Result.failure(Exception("User is null")))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseAuth", "signInWithCredential:failure", e)
                    cont.resume(Result.failure(e))
                }
        }
    }



}
