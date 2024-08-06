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


    fun setLikedCount(uid : String, isLikeds : Boolean){
        val ref = firebaseFirestore.collection("posts").document(uid)
        ref.addSnapshotListener{snapshot, e ->
            if(e != null){
                return@addSnapshotListener
            }
            if(snapshot != null && snapshot.exists()){
                Log.d("FeedViewModel","데이터 : ${snapshot.data}")
                val data = snapshot.data?.toDataClass<FeedModel>()
                _likeCount.value = data?.likedCount
                _isLiked.value = data?.isLiked
            }else{
                Log.d("FeedViewModel","데이터 : null")
            }
        }

        ref
            .update("likedCount",if(isLikeds){
               likeCount.value?.plus(1)
            }else{
                likeCount.value?.minus(1)
            })
            .addOnSuccessListener { Log.d("FeedViewModel","LikedCount 변경 성공") }
            .addOnFailureListener { Log.d("FeedViewModel","LikedCount 변경 실패")  }

        ref
            .update("isLiked",isLikeds)
            .addOnSuccessListener { Log.d("FeedViewModel","LikedCount 변경 성공") }
            .addOnFailureListener { Log.d("FeedViewModel","LikedCount 변경 실패")  }

    }

    fun getLikedCount(uid: String){
        val ref = firebaseFirestore.collection("posts").document(uid)

    }

    private inline fun <reified T> Map<String, Any>.toDataClass(): T? {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, T::class.java)
    }

}