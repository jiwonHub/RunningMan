package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.CommentModel
import com.cjwjsw.runningman.domain.model.FeedModel
import com.cjwjsw.runningman.domain.model.LikeModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
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

    private val _commentArr = MutableLiveData<MutableList<CommentModel>?>()

    val commentArr : LiveData<MutableList<CommentModel>?> get() = _commentArr

    private val _feed_time = MutableLiveData<String>()
    val feed_time : LiveData<String> get() = _feed_time


    fun getLikedCount(uid : String){
        val ref = firebaseFirestore.collection("posts").document(uid)

        //데이터 받아오기
        ref.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                Log.d("DetailFeedViewModel", "받아온 데이터: ${document.data}")
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

                Log.d("DetailFeedViewModel",newIsLiked.toString())

                //좋아요 카운트 업
                ref.update("likedCount", newCount,"isLiked",newIsLiked)
                    .addOnSuccessListener {
                        Log.d("DetailFeedViewModel", "LikedCount 업데이트 성공")
                        _likeCount.value = newCount // Update the LiveData to reflect the new count
                        _isLiked.value = newIsLiked
                    }
                    .addOnFailureListener { Log.d("DetailFeedViewModel", "LikedCount 업데이트 실패") }
            } else {
                Log.d("DetailFeedViewModel", "해당하는 UID의 피드가 없습니다")
            }
        }.addOnFailureListener { e ->
            Log.w("DetailFeedViewModel", "피드를 불러오는중 오류 발생", e)
        }
    }

    fun fetchCommentData(uid : String){
        val ref = fbRef.getReference(uid).child("comments")
        Log.d("DetailFeedViewModel", "uid = $uid")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentList = mutableListOf<CommentModel>()
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(CommentModel::class.java)
                    if (comment != null) {
                        commentList.add(comment)
                    }
                }
                _commentArr.value = commentList // _commentArr은 LiveData<List<Comment>> 타입
                Log.d("DetailFeedViewModel","댓글 불러오기 성공 : ${commentArr.value.toString()}")
                Log.d("DetailFeedViewModel","댓글 불러오기 성공 : ${snapshot.value.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DetailFeedViewModel","댓글 불러오기 실패 : ${error.message}")
            }

        })
    }

    fun uploadComment(comment: String, feedUid: String, userName: String,profileImg: String,userNumber: String) {
        val ref = FeedViewModel.fbRef.getReference(feedUid).child("comments")
        val newComment = CommentModel( //데이터 만들기
            comment = comment,
            timestamp = System.currentTimeMillis() / 1000,
            userName = userName,
            profileUrl = profileImg,
            userUid = userData?.idToken.toString(),
            userNumber = userData?.userNumber.toString()
        )

        val newCommentKey = ref.push().key //고유 키 생성
        if(newCommentKey != null) {
            ref.child(newCommentKey).setValue(newComment).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("DetailFeedViewModel", "Comment added successfully")
                } else {
                    Log.d("DetailFeedViewModel", "Failed to add comment: ${task.exception}")
                }
            }
        }
    }

    fun uploadLikeCount(userUid : String, feedUid : String){ //좋아요 등록
        val ref = fbRef.getReference(feedUid).child("like")
        val newLike = LikeModel( //데이터 만들기
            userUid = userUid,
            feedUid = feedUid
        )

        if(userUid.isNotEmpty()){
            ref.child(userUid).setValue(newLike).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.d("DetailFeedViewModel","Liked added successfully")
                } else{
                    Log.d("DetailFeedViewModel","Failed to add Liked : ${task.exception}")
                }
            }
        }
    }

    fun deleteLikeCount(userUid : String, feedUid : String){
        val ref = fbRef.getReference(feedUid).child("like")

        if(userUid.isNotEmpty()){
            ref.child(userUid).removeValue().addOnSuccessListener { task ->
                try{
                    Log.d("DetailFeedViewModel","Remove Liked Complete")
                }catch (e : Exception){
                    Log.d("DetailFeedViewModel","remove task is failed : ${e.message}" )
                }
            }
        }else{
            Log.d("DetailFeedViewModel","Remove Liked Failed : ${userUid}")
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




    private inline fun <reified T> Map<String, Any>.toDataClass(): T? {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, T::class.java)
    }

    companion object{
        val userData =  UserManager.getInstance()
        val fbRef = Firebase.database
    }

}