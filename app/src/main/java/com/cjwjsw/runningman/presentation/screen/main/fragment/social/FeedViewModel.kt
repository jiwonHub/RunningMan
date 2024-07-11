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
import com.google.firebase.database.getValue
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
        val postListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) { //데이터 가져오기
                val post = snapshot.getValue<MutableList<CommentModel>>()
                _commentArr.value = post
            }

            override fun onCancelled(error: DatabaseError) { //데이터 접근 불가 시
                Log.d("FeedViewModel", "loadPost:onCancelled ${error.toException()}")
            }

        }

    }

    fun uploadComment(comment : String, uid : String ) {
        fbRef.child(uid).child(userUid.toString()).push().setValue(comment).addOnSuccessListener {
            Log.d("FeedViewModel","댓글 저장 성공 : $comment")
        }.addOnFailureListener {
            Log.d("FeedViewModel","댓글 저장 실패 : ${it.message.toString()}")
        }
    }


    fun charToString(uid: MutableList<Char>) : String{
        Log.d("onclick",uid.toString())
        var transUID = uid.filter { it != ',' && it != ' ' }.joinToString("")
        return transUID
    }

    private inline fun <reified T> Map<String, Any>.toDataClass(): T? {
            val json = Gson().toJson(this)
            return Gson().fromJson(json, T::class.java)
    }

    companion object{
        val fbRef = Firebase.database.getReference("comment")
    }
}