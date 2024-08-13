package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.CommentModel
import com.cjwjsw.runningman.domain.model.FeedModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

    private val _commentArr = MutableLiveData<MutableList<CommentModel>?>()

    val commentArr : LiveData<MutableList<CommentModel>?> get() = _commentArr


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

    fun fetchCommentData(uid : String){
        val ref = FeedViewModel.fbRef.getReference(uid).child("comments")
        Log.d("FeedViewModel", "uid = $uid")

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
                Log.d("FeedViewModel","댓글 불러오기 성공 : ${commentArr.value.toString()}")
                Log.d("FeedViewModel","댓글 불러오기 성공 : ${snapshot.value.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FeedViewModel","댓글 불러오기 실패 : ${error.message}")
            }

        })
    }

    fun uploadComment(comment: String, feedUid: String, userName: String,profileImg: String) {
        val ref = FeedViewModel.fbRef.getReference(feedUid).child("comments")
        val newComment = CommentModel(
            comment = comment,
            timestamp = System.currentTimeMillis() / 1000,
            userName = userName,
            profileUrl = profileImg,
            userUid = userData?.idToken.toString()
        )

        val newCommentKey = ref.push().key
        if(newCommentKey != null) {
            ref.child(newCommentKey).setValue(newComment).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FeedViewModel", "Comment added successfully")
                } else {
                    Log.d("FeedViewModel", "Failed to add comment: ${task.exception}")
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
    }

}