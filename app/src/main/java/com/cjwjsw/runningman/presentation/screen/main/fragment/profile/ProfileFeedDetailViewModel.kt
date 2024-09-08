package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.CommentModel
import com.cjwjsw.runningman.domain.model.FeedModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileFeedDetailViewModel @Inject constructor( private val firebaseFirestore: FirebaseFirestore) :
    ViewModel(){
    private val _likeCount = MutableLiveData<Int>()
    val likeCount: LiveData<Int> get() = _likeCount

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked : LiveData<Boolean> get() = _isLiked

    private val _commentArr = MutableLiveData<MutableList<CommentModel>?>()

    val commentArr : LiveData<MutableList<CommentModel>?> get() = _commentArr

    private val _feed_time = MutableLiveData<String>()
    val feed_time : LiveData<String> get() = _feed_time


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

                //좋아요 카운트 업
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

    fun getFeedUploadTime(uid : String){
        val ref = firebaseFirestore.collection("posts").document(uid)
        ref.get().addOnSuccessListener {document ->
            if (document != null && document.exists()) {
                Log.d("FeedViewModel", "받아온 데이터: ${document.data}")
                val data = document.data?.toDataClass<FeedModel>()

                val time = data?.timestamp
                Log.d("DetailFeedViewModel",time.toString())

                // 피드 업로드 시간 가져오기
                val uploadTimeInMillis = time?.seconds?.times(1000L) ?: 0L

                // 현재 시간 가져오기
                val currentTimeInMillis = System.currentTimeMillis()

                // 업로드 시간과 현재 시간 뺴서 차이 구하기
                val timeDifferenceInMillis = currentTimeInMillis - uploadTimeInMillis

                // 밀리 세컨드 시간으로 변경
                val hoursPassed = timeDifferenceInMillis / (1000 * 60 * 60)

                // 밀리세컨드 분으로 변경
                val minute = timeDifferenceInMillis / (1000 * 60)

                if(hoursPassed in 1..12){
                    //12시간이 안지났다면
                    _feed_time.value = "${hoursPassed.toInt()}시간 전"
                }else if(hoursPassed > 12){
                    //12시간 초과
                    _feed_time.value =  "${hoursPassed.toInt() / 12}일 전"
                }else{
                    // 1시간 미만이라면
                    _feed_time.value = "${minute.toInt()}분 전"
                }
            }
        }
    }

    fun deleteFeed(feedUid : String){
        val ref = firebaseFirestore.collection("posts").document(feedUid)
        ref.delete().addOnSuccessListener {task ->
            Log.d("ProfileFeedDetailViewModel","피드 삭제 성공")
        }.addOnFailureListener { e ->
            Log.d("ProfileFeedDetailViewModel","피드 삭제 성공")
        }
    }





    private inline fun <reified T> Map<String, Any>.toDataClass(): T? {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, T::class.java)
    }

    companion object{
        val userData =  UserManager.getInstance()
    }

}