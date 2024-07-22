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
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : ViewModel() {

    private val _feedArr = MutableLiveData<MutableList<FeedModel>>()
    val feedArr: LiveData<MutableList<FeedModel>> get() = _feedArr

    private val _commentArr = MutableLiveData<MutableList<CommentModel>?>()
    val commentArr : LiveData<MutableList<CommentModel>?> get() = _commentArr
    private val userUid = UserManager.getInstance()?.id


    private val arr : MutableList<FeedModel> = mutableListOf()
    fun fetchFeedData() {
        firebaseFirestore.collection("posts").orderBy("timestamp",Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener {documents ->
                if(documents == null){
                    Log.d("전체 피드 정보가 없습니다","0")
                }else{
                    for(i in 0 until documents.size()){
                        val ref = documents.documents[i].data?.toDataClass<FeedModel>()
                        Log.d("FeedViewModel","데이터 정상 변환\t${ref.toString()}")
                        if(i > 15){
                            break;
                        }
                        if (ref != null) {
                            Log.d("FeedViewModel","데이터 배열에 삽입\t${ref.toString()}삽입")
                            arr.add(ref)
                        }
                    }
                    Log.d("FeedViewModel","최종적으로 삽입되는 데이터 수 ${arr.size}")
                    _feedArr.value = arr
                }
            }
            .addOnFailureListener {e ->
                Log.d("FeedViewModel","피드 정보 불러오기 실패 : ${e.toString()}")
            }
    }

    fun fetchCommentData(uid : String){
        val ref = fbRef.getReference(uid).child("comments")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentList = mutableListOf<CommentModel>()
                for (commentSnapshot in snapshot.children) {
                    val comment = commentSnapshot.getValue(CommentModel::class.java)
                    if (comment != null) {
                        commentList.add(comment)
                    }
                }
                _commentArr.value = commentList // _commentArr은 LiveData<List<Comment>> 타입
                Log.d("FeedViewModel","댓글 불러오기 성공 : ${snapshot.value.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FeedViewModel","댓글 불러오기 실패 : ${error.message}")
            }

        })



//        val ref = fbRef.getReference(uid)
//        ref.child(uid).get().addOnSuccessListener {snapshot ->
//            val commentList = mutableListOf<CommentModel>()
//            for(data in snapshot.children){
//                val comment = data.getValue(CommentModel::class.java)
//                Log.d("FeedViewModel","snapShot CommentModel로 캐스팅 성공 : ${comment.toString()}")
//                commentList.add(comment!!)
//            }
//            _commentArr.value = commentList
//            Log.d("FeedViewModel","댓글 불러오기 성공 : ${snapshot.value.toString()}")
//        }.addOnFailureListener {
//            Log.d("FeedViewModel","댓글 불러오기 실패 : ${it.message.toString()}")
//        }

//        val commentRef = fbRef.getReference(uid)
//        commentRef.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val commentList = mutableListOf<CommentModel>()
//                Log.d("FeedViewModel","스냅샷 : ${snapshot.value}")
//                for(commentSnapshot in snapshot.children){
//                    val comment = commentSnapshot.getValue(CommentModel::class.java)
//                    Log.d("FeedViewModel","댓글 정보 불러오기 성공2 : $comment")
//                    if(comment!=null){
//                        commentList.add(comment)
//                    }
//                }
//                _commentArr.value = commentList
//                Log.d("FeedViewModel","댓글 정보 불러오기 성공 : $commentList")
//
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("FeedViewModel","댓글 정보 불러오기 실패 : ${error.toString()}")
//            }
//
//        })

    }

    fun uploadComment(comment : String, feedUid : String ) {
        val ref = fbRef.getReference(feedUid).child("comments")
        val newComment = CommentModel(
            comment = comment,
            timestamp = System.currentTimeMillis() / 1000,
            userUid = userUid.toString()
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

//        val comment = translateData(comment) //코멘트 정의
//        val final : HashMap<String,Comments> = hashMapOf()
//        final.put(userUid.toString(),comment)
//
//        ref.updateChildren(final as Map<String, Any>).addOnSuccessListener {
//            Log.d("FeedViewModel","댓글 저장 성공 : $comment")
//        }.addOnFailureListener {
//            Log.d("FeedViewModel","댓글 저장 실패 : ${it.message.toString()}")
//        }
    }


    fun charToString(uid: MutableList<Char>) : String{
        Log.d("onclick",uid.toString())
        var transUID = uid.filter { it != ',' && it != ' ' }.joinToString("")
        return transUID
    }

//    fun translateData(com: String): Comments { //Comments 데이터 타입 변경
//        val time = Timestamps(
//            nanoseconds = System.currentTimeMillis() * 1000000 % 1000000000,
//            seconds = System.currentTimeMillis() / 1000
//        )
//        val comArr : MutableList<String> = mutableListOf()
//        comArr.add(com)
//
//        val arr = Comments(comment = comArr, userUid = userUid.toString(), timestamp = time)
//        return arr
//    }

    private inline fun <reified T> Map<String, Any>.toDataClass(): T? {
            val json = Gson().toJson(this)
            return Gson().fromJson(json, T::class.java)
    }

    companion object{
        val fbRef = Firebase.database
    }
}