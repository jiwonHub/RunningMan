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
    ViewModel() {
    private val _likeCount = MutableLiveData<Int>()
    val likeCount: LiveData<Int> get() = _likeCount

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean> get() = _isLiked

    private val _commentArr = MutableLiveData<MutableList<CommentModel>?>()

    val commentArr: LiveData<MutableList<CommentModel>?> get() = _commentArr

    private val _feed_time = MutableLiveData<String>()
    val feed_time: LiveData<String> get() = _feed_time

    fun isLiked(feedUid: String, userUid: String, callback: (Boolean) -> Unit) { // 좋아요 되어있는지 체크
        val ref = fbRef.getReference(feedUid).child("like")

        ref.child(userUid).get().addOnCompleteListener { snapShot ->
            if (snapShot.result.exists()) { // 만약 유저의 좋아요 정보가 있다면,
                Log.d("DetailFeedViewModel", "유저의 좋아요 정보가 있습니다 : ${snapShot.result.exists()}")
                callback(false)
            } else { // 좋아요 정보가 없다면 1
                Log.d("DetailFeedViewModel", "유저의 좋아요 정보가 없습니다 : ${snapShot.result.exists()}")
                callback(true)
            }
        }
    }


    fun setLikedCount(feedUid: String, userUid: String, userName: String) { // RDB에 좋아요 카운트 등록
        val ref = fbRef.getReference(feedUid).child("like")
        isLiked(feedUid = feedUid, userUid = userUid) { isLiked ->
            Log.d("DetailFeedViewModel", "isLiked : $isLiked") // 2

            if (isLiked) { // 저장이 되어있지 않다면,
                val user = LikeModel( //저장할 데이터 셋 만들기
                    userUid = userUid,
                    userName = userName
                )

                ref.child(userUid).setValue(user).addOnCompleteListener { task ->
                    Log.d("DetailFeedViewModel", "좋아요 등록 성공")

                    //getRD 메서드 비동기 구현
                    getRDLikedCount(feedUid) { RDlikeCount ->
                        _likeCount.value = RDlikeCount
                        _isLiked.value = RDlikeCount > 0
                        setFeedLikedCount(feedUid) // 피드 정보에 좋아요 카운트 등록
                    }
                }
            } else { // 좋아요가 저장되어있다면
                Log.d("DetailFeedViewModel", "좋아요 삭제 성공") //3
                removeLikedCount(
                    userUid = userUid,
                    feedUid = feedUid
                )
            }
        }
    }

    fun setFeedLikedCount(feedUid: String) {
        val ref = firebaseFirestore.collection("posts").document(feedUid)

        ref.update("likedCount", _likeCount.value?.plus(1))
            .addOnCompleteListener { task ->
                Log.d("DetailFeedViewModel", "피드 정보에 좋아요 카운트 등록 완료 : ${task.result}")
            }
            .addOnFailureListener { e ->
                Log.d("DetailFeedViewModel", "피드 정보에 좋아요 카운트 등록 실패 : ${e.message}")
            }
    }

    fun removeLikedCount(userUid: String, feedUid: String) {
        val ref = fbRef.getReference(feedUid).child("like")

        ref.child(userUid).removeValue().addOnCompleteListener { task ->
            Log.d("DetailFeedViewModel", "좋아요 카운트 삭제 완료: ${task.result}") // 4
        }.addOnFailureListener { e ->
            Log.d("DetailFeedViewModel", "좋아요 카운트 삭제 실패 : ${e.message}")
        }

    }

    fun getRDLikedCount(feedUid: String, callback: (Int) -> Unit) {
        val ref = fbRef.getReference(feedUid).child("like")

        ref.get().addOnSuccessListener { snapshot ->
            val likedCount = snapshot.childrenCount.toInt()
            callback(likedCount)
        }.addOnFailureListener { e ->
            Log.d("DetailFeedViewMode", "RD에서 좋아요 카운트 불러오기 실패 :${e.message}")
            callback(0)
        }
    }

    fun getFeedLikedCount(feedUid: String) {
        val ref = firebaseFirestore.collection("posts").document(feedUid) // 클릭한 피드 데이터 참조 생성

        ref.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                Log.d("DetailFeedViewModel", "좋아요 받아온 데이터: ${document.data}")
                val data = document.data?.toDataClass<FeedModel>()

                //가져온 데이터 라이브 데이터에 저장
                _isLiked.value = data?.isLiked
                _likeCount.value = data?.likedCount
            }
        }
    }

//    fun getLikedCount(uid : String){
//        val ref = firebaseFirestore.collection("posts").document(uid)
//
//        //데이터 받아오기
//        ref.get().addOnSuccessListener { document ->
//            if (document != null && document.exists()) {
//                Log.d("DetailFeedViewModel", "받아온 데이터: ${document.data}")
//                val data = document.data?.toDataClass<FeedModel>()
//
//                val currentLikeCount = data?.likedCount ?: 0
//                val currentIsLiked = data?.isLiked ?: false
//
//                // 받아온 데이터 처리
//                val newCount = if (currentIsLiked) {
//                    currentLikeCount -1
//                } else {
//                    currentLikeCount +1
//                }
//
//                val newIsLiked = !currentIsLiked
//
//                Log.d("DetailFeedViewModel",newIsLiked.toString())
//
//                //좋아요 카운트 업
//                ref.update("likedCount", newCount,"isLiked",newIsLiked)
//                    .addOnSuccessListener {
//                        Log.d("DetailFeedViewModel", "LikedCount 업데이트 성공")
//                        _likeCount.value = newCount // Update the LiveData to reflect the new count
//                        _isLiked.value = newIsLiked
//                    }
//                    .addOnFailureListener { Log.d("DetailFeedViewModel", "LikedCount 업데이트 실패") }
//            } else {
//                Log.d("DetailFeedViewModel", "해당하는 UID의 피드가 없습니다")
//            }
//        }.addOnFailureListener { e ->
//            Log.w("DetailFeedViewModel", "피드를 불러오는중 오류 발생", e)
//        }
//    }

    fun fetchCommentData(uid: String) {
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
                Log.d("DetailFeedViewModel", "댓글 불러오기 성공 : ${commentArr.value.toString()}")
                Log.d("DetailFeedViewModel", "댓글 불러오기 성공 : ${snapshot.value.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("DetailFeedViewModel", "댓글 불러오기 실패 : ${error.message}")
            }

        })
    }

    fun uploadComment(
        comment: String,
        feedUid: String,
        userName: String,
        profileImg: String,
        userNumber: String
    ) {
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
        if (newCommentKey != null) {
            ref.child(newCommentKey).setValue(newComment).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("DetailFeedViewModel", "Comment added successfully")
                } else {
                    Log.d("DetailFeedViewModel", "Failed to add comment: ${task.exception}")
                }
            }
        }
    }

    fun getFeedUploadTime(uid: String) {
        val ref = firebaseFirestore.collection("posts").document(uid)
        ref.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                Log.d("FeedViewModel", "받아온 데이터: ${document.data}")
                val data = document.data?.toDataClass<FeedModel>()

                val time = data?.timestamp
                Log.d("DetailFeedViewModel", time.toString())

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

                if (hoursPassed in 1..12) {
                    //12시간이 안지났다면
                    _feed_time.value = "${hoursPassed.toInt()}시간 전"
                } else if (hoursPassed > 12) {
                    //12시간 초과
                    _feed_time.value = "${hoursPassed.toInt() / 12}일 전"
                } else {
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

    companion object {
        val userData = UserManager.getInstance()
        val fbRef = Firebase.database
    }

}
