package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.domain.model.FeedModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailFeedViewModel @Inject constructor( private val firebaseFirestore: FirebaseFirestore) :
    ViewModel(){
    private val _likeCount = MutableLiveData<Int>()
    val likeCount: LiveData<Int> get() = _likeCount

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked : LiveData<Boolean> get() = _isLiked


    fun getLikedCount(uid : String){
        val ref = firebaseFirestore.collection("posts").document(uid)

        //데이터 받아오기
        ref.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                Log.d("FeedViewModel", "받아온 데이터: ${document.data}")
                val data = document.data?.toDataClass<FeedModel>()

                val currentLikeCount = data?.likedCount ?: 0
                val currentIsLiked = data?.isLiked ?: false

                // 받아온 데이터 처리
                val newCount = if (currentIsLiked) {
                    currentLikeCount -1
                } else {
                    currentLikeCount +1
                }

                val newIsLiked = !currentIsLiked

                Log.d("FeedViewModel",newIsLiked.toString())

                ref.update("likedCount", newCount,"isLiked",newIsLiked)
                    .addOnSuccessListener {
                        Log.d("FeedViewModel", "LikedCount 업데이트 성공")
                        _likeCount.value = newCount // Update the LiveData to reflect the new count
                        _isLiked.value = newIsLiked
                    }
                    .addOnFailureListener { Log.d("FeedViewModel", "LikedCount 업데이트 실패") }
            } else {
                Log.d("FeedViewModel", "해당하는 UID의 피드가 없습니다")
            }
        }.addOnFailureListener { e ->
            Log.w("FeedViewModel", "피드를 불러오는중 오류 발생", e)
        }
    }


    private inline fun <reified T> Map<String, Any>.toDataClass(): T? {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, T::class.java)
    }

}