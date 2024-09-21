package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.FeedModel
import com.cjwjsw.runningman.domain.repository.WalkRepository
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fbManager:FirebaseStorage,
    private val fbsManager : FirebaseFirestore,
    private val db : WalkRepository
): ViewModel() {
    private val _photoArr = MutableLiveData<MutableList<Uri>>().apply { value = mutableListOf() }
    val arr : MutableList<FeedModel> = mutableListOf()
    val photoArr: LiveData<MutableList<Uri>> get() = _photoArr

    private val _feedArr = MutableLiveData<MutableList<FeedModel>?>()
    val feedArr: LiveData<MutableList<FeedModel>?> get() = _feedArr

    private val _totalWalkArr = MutableLiveData<Int>()
    val totalWalkArr : LiveData<Int> get() = _totalWalkArr

    private val _avgWalkArr = MutableLiveData<Int>()
    val avgWalkArr : LiveData<Int> get() = _avgWalkArr

    private val userUid : String = userData?.idToken.toString()
    private val profileImg : String = userData?.profileUrl.toString()
    private val userName : String = userData?.nickName.toString()
    private val userNumber : String = userData?.userNumber.toString()

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> get() = _uploadStatus

     fun getAllWalkData(){ // 전체 걸음 수 가져오기
         viewModelScope.launch {
             val x = db.getAllWalks()
             var sum = 0;

             for (i : Int in 0..< x.size){
                sum+= x[i].stepCount
             }
             _totalWalkArr.value = sum
             Log.d(TAG,"전체 걸음 수 : $sum")
         }
     }

    fun getAvgWalkData(){ // 평균 걸음 수 가져오기
        viewModelScope.launch {
            val x = db.getAllWalks()
            var sum = 0;
            for (i : Int in 0..< x.size){
                sum+= x[i].stepCount
            }
            Log.d(TAG,"사이즈 : ${x.size}")

            if(x.isEmpty()){
                _avgWalkArr.value = 0
            }else{
                _avgWalkArr.value = (sum / x.size)
            }
        }

    }

    fun setImageFile(uri : Uri){
        val currentList = _photoArr.value ?: mutableListOf()
        currentList.add(uri)
        _photoArr.value = currentList
        Log.d("ProfileViewModel", uri.toString())
    }

    fun uploadPost(title: String, content: String) {
        val currentTime = System.currentTimeMillis()
        Log.d("ProfileViewModel", "시작 시간: $currentTime")
        Log.d("ProfileViewModel", userUid)

        val imageUris = _photoArr.value ?: return
        val feedUID = UUID.randomUUID().toString()

        // 1. 이미지 병렬 업로드 처리
        val uploadTasks = imageUris.mapIndexed { index, imgUri ->
            val imageRef = fbManager.reference.child("Post/$userUid-$feedUID-$index.jpg")
            imageRef.putFile(imgUri).continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("이미지 업로드 실패")
                }
                imageRef.downloadUrl
            }
        }

        // 2. 이미지 업로드 완료 후 피드 메타데이터 저장
        Tasks.whenAllSuccess<Uri>(uploadTasks).addOnSuccessListener { imageUrls ->
            val imageUrlStrings = imageUrls.map { it.toString() }
            savePostMetadata(userUid, title, content, imageUrlStrings, feedUID, profileImg, userName, 0, false, userData!!.idToken, userNumber)
        }.addOnFailureListener { e ->
            _uploadStatus.value = false
            Log.d("ProfileViewModel", "이미지 업로드 실패: ${e.message}")
        }
    }

    private fun savePostMetadata(
        postId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        feedUID: String,
        profileImg: String,
        userName: String,
        likedCount: Int,
        isLiked: Boolean,
        userUID: String,
        userNumber: String
    ) {
        val postMetadata = hashMapOf(
            "postId" to postId,
            "title" to title,
            "content" to content,
            "imageUrls" to imageUrls,
            "timestamp" to FieldValue.serverTimestamp(),
            "feedUID" to feedUID,
            "profileURL" to profileImg,
            "userName" to userName,
            "likedCount" to likedCount,
            "isLiked" to isLiked,
            "userUID" to userUID,
            "userNumber" to userNumber
        )

        // 3. Firestore에 메타데이터 저장
        fbsManager.collection("posts").document(feedUID)
            .set(postMetadata)
            .addOnSuccessListener {
                _uploadStatus.value = true
                _photoArr.value = mutableListOf()
                Log.d("ProfileViewModel", "피드 업로드 성공")
                updateUIWithNewPost(postMetadata)  // Firestore 업데이트 전에 UI 반영
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = false
                Log.d("ProfileViewModel", "피드 업로드 실패: ${e.message}")
            }
    }

    // UI를 즉시 업데이트하는 함수
    private fun updateUIWithNewPost(postMetadata: HashMap<String, Any>) {
        val newPost = postMetadata.toDataClass<FeedModel>()
        if (newPost != null) {
            val currentFeed = _feedArr.value?.toMutableList() ?: mutableListOf()
            currentFeed.add(0, newPost)  // 피드를 맨 위에 추가
            _feedArr.value = currentFeed
        }
    }
    fun getUserFeed(){
        fbsManager.collection("posts").whereEqualTo("postId",userUid)
            .get()
            .addOnSuccessListener {document ->
                if(document == null){
                    Log.d("ProfileViewModel","해당하는 유저의 저장된 피드는 없음")
                }else{
                    for(i in 0..<document.documents.size) {
                        val ref = document.documents[i].data?.toDataClass<FeedModel>()
                        if (ref != null) {
                            arr.add(ref)
                        }
                        Log.d("ProfileView1", ref.toString())
                    }
                    _feedArr.value = arr
                }
                Log.d("ProfileView2",feedArr.value.toString())
            }
            .addOnFailureListener {
                Log.d("ProfileViewModel","파이어베이스에서 유저 피드 정보 호출 실패 +${it.toString()}")
            }
    }



    fun charToString(uid: MutableList<Char>) : String{
        Log.d("onclick",uid.toString())
        var transUID = uid.filter { it != ',' && it != ' ' }.joinToString("")
        return transUID
    }



    inline fun <reified T> Map<String, Any>.toDataClass(): T {
        val json = JSONObject(this).toString()
        return Gson().fromJson(json, T::class.java)
    }

    companion object{
        const val TAG = "ProfileViewModel"
        val userData = UserManager.getInstance()
    }

}
