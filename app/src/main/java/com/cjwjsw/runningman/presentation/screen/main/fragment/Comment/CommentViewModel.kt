package com.cjwjsw.runningman.presentation.screen.main.fragment.Comment

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.CommentModel
import com.cjwjsw.runningman.presentation.screen.main.fragment.social.DetailFeedViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor() : ViewModel() {

    fun uploadComment(
        comment: String,
        feedUid: String,
        userName: String,
        profileImg: String,
        userNumber: String
    ) {
        val ref = fbRef.getReference(feedUid).child("comments")
        val newCommentKey = ref.push().key //고유 키 생성
        val newComment = CommentModel( //데이터 만들기
            comment = comment,
            timestamp = System.currentTimeMillis() / 1000,
            userName = userName,
            profileUrl = profileImg,
            userUid = userid.toString(),
            userNumber = userNumber,
            newCommentKey = newCommentKey.toString()
        )

        if (newCommentKey != null) {
            ref.child(newCommentKey).setValue(newComment).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Comment added successfully")
                } else {
                    Log.d(TAG, "Failed to add comment: ${task.exception}")
                }
            }
        }
    }

    fun deleteComment(feedUid : String,commentKey : String){
        Log.d(TAG,"댓글 삭제 : $commentKey")
        val ref = DetailFeedViewModel.fbRef.getReference(feedUid).child("comments")

        ref.child(commentKey).removeValue().addOnSuccessListener {
            Log.d(TAG,"댓글 삭제 완료")
        }.addOnFailureListener { e ->
            Log.d(TAG,"댓글 삭제 실패 :${e.message}")
        }
    }

    companion object{
        val fbRef = Firebase.database
        val userInfo = UserManager.getInstance()
        val userid = userInfo?.idToken
        val userNumber = userInfo?.userNumber
        val TAG = "CommentViewModel"
    }

}