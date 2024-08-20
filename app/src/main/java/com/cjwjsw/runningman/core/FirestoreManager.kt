package com.cjwjsw.runningman.core

import android.util.Log
import com.cjwjsw.runningman.domain.model.WalkModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun saveWalk(walk: WalkModel) {
        try {
            val userId = UserManager.getInstance()?.idToken ?: 1
            val userDocRef = firestore.collection("walks").document(userId.toString())

            // userId 문서 아래에 dates 컬렉션을 만들고, 날짜를 문서 ID로 설정하여 walk 데이터를 저장
            userDocRef.collection("dates").document(walk.date).set(walk).await()
        } catch (e: Exception) {
            Log.e("firestoreManager", "Error saving walk to Firestore: ${e.message}")
        }
    }

    suspend fun getWalkByDate(date: String): WalkModel? {
        val documentSnapshot = firestore.collection("walks").document(date).get().await()
        return documentSnapshot.toObject(WalkModel::class.java)
    }
}