package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.domain.model.FeedModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _feedArr = MutableLiveData<MutableList<FeedModel>>()
    val feedArr: LiveData<MutableList<FeedModel>> get() = _feedArr
    fun fetchFeedData() {

        // Firestore 캐싱 활성화 설정
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // 캐싱 활성화
            .build()

        firebaseFirestore.firestoreSettings = settings

        // 리얼 타임 리스너
        firebaseFirestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("FeedViewModel", "리얼타임 데이터 불러오기 실패: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    Log.d("FeedViewModel", "리얼타임 데이터 가져오기 성공")
                    _feedArr.value = snapshots.documents.mapNotNull { it.data?.toDataClass<FeedModel>() }.toMutableList()
                } else {
                    Log.d("FeedViewModel", "리얼타임 데이터가 없습니다")
                }
            }

        // 캐시에서 정보 가져오기
        firebaseFirestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get(Source.CACHE)  // 로컬 캐시에서 먼저 데이터 가져오기
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FeedViewModel", "캐시에 피드 정보가 없습니다")
                } else {
                    Log.d("FeedViewModel", "캐시에서 데이터 가져오기 성공")
                    _feedArr.value = documents.documents.mapNotNull { it.data?.toDataClass<FeedModel>() }.toMutableList()
                }
            }
            .addOnFailureListener { e ->
                Log.d("FeedViewModel", "캐시에서 데이터 불러오기 실패: ${e.message}")
            }

        // Firebase 서버에서 최신 데이터 가져오기
        firebaseFirestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get(Source.SERVER)  // 서버에서 최신 데이터 가져오기
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FeedViewModel", "서버에 피드 정보가 없습니다")
                } else {
                    Log.d("FeedViewModel", "서버에서 데이터 가져오기 성공")
                    _feedArr.value = documents.documents.mapNotNull { it.data?.toDataClass<FeedModel>() }.toMutableList()
                }
            }
            .addOnFailureListener { e ->
                Log.d("FeedViewModel", "서버에서 데이터 불러오기 실패: ${e.message}")
            }
    }

    //UID String 변환 함수
    fun charToString(uid: MutableList<Char>) : String{
        Log.d("onclick",uid.toString())
        var transUID = uid.filter { it != ',' && it != ' ' }.joinToString("")
        return transUID
    }

    //Json to Gson 변환 함수
    private inline fun <reified T> Map<String, Any>.toDataClass(): T? {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, T::class.java)
    }
}