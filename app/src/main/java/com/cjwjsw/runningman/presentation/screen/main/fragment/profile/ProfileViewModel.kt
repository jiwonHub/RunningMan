package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Source
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fbManager:FirebaseStorage,
    private val fbsManager : FirebaseFirestore,
    private val db : WalkRepository,
    private val application : Application
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

    private val _isPosted : MutableStateFlow<PostedState> = MutableStateFlow(PostedState.beforePosted)
    val isPosted : StateFlow<PostedState> get() = _isPosted.asStateFlow()

    private val userUid : String = userData?.idToken.toString()
    private val profileImg : String = userData?.profileUrl.toString()
    private val userName : String = userData?.nickName.toString()
    private val userNumber : String = userData?.userNumber.toString()



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
        Log.d("ProfileViewModel", "uri : ${uri.toString()}")
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun convertResizeImage(imageUri: Uri): Uri { // 비트맵 이미지 리사이징 함수
        val bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver, imageUri)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width / 2, bitmap.height / 2, true)

        val byteArrayOutputStream = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 90, byteArrayOutputStream)

        val tempFile = File.createTempFile("resized_image", ".jpg", application.cacheDir)
        val fileOutputStream = FileOutputStream(tempFile)

        fileOutputStream.write(byteArrayOutputStream.toByteArray())
        fileOutputStream.close()

        return Uri.fromFile(tempFile)
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun uploadPost(title: String, content: String) {
        val currentTime = System.currentTimeMillis()

        _isPosted.value = PostedState.Loading // 업로드 로딩 시작

        Log.d("ProfileViewModel", "시작 시간: $currentTime")
        Log.d("ProfileViewModel", userUid)

        val imageUris = _photoArr.value ?: return
        val feedUID = UUID.randomUUID().toString()

        // 1. 이미지 병렬 업로드 처리
        val uploadTasks = imageUris.mapIndexed { index, imgUri ->
            val imageRef = fbManager.reference.child("Post/$userUid-$feedUID-$index.jpg")
            imageRef.putFile(convertResizeImage(imgUri)).addOnProgressListener {
                Log.d(TAG,"업로드 걸린시간 : ${100 * it.bytesTransferred / it.totalByteCount}")
            }.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("이미지 업로드 실패")
                }
                imageRef.downloadUrl
            }
        }



        // 2. 이미지 업로드 완료 후 피드 메타데이터 저장
        Tasks.whenAllSuccess<Uri>(uploadTasks).addOnSuccessListener { imageUrls ->
            val imageUrlStrings = imageUrls.map { it.toString() }
            val postMetadata = hashMapOf(
                "postId" to userUid,
                "title" to title,
                "content" to content,
                "imageUrls" to imageUrls,
                "timestamp" to FieldValue.serverTimestamp(),
                "feedUID" to feedUID,
                "profileURL" to profileImg,
                "userName" to userName,
                "likedCount" to 0,
                "isLiked" to false,
                "userUID" to userData!!.idToken,
                "userNumber" to userNumber
            )
            savePostMetadata(userUid, title, content, imageUrlStrings, feedUID, profileImg, userName, 0, false, userData!!.idToken, userNumber)
        }.addOnFailureListener { e ->
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
                _photoArr.value = mutableListOf()
                _isPosted.value = PostedState.Success // 업로드 상태 성공으로 변경
                Log.d("ProfileViewModel", "피드 업로드 성공")
                updateUIWithNewPost(postMetadata) // 파베 업로드 후 재호출 없이 UI 바로 반영
            }
            .addOnFailureListener { e ->
                Log.d("ProfileViewModel", "피드 업로드 실패: ${e.message}")
                _isPosted.value = PostedState.Failure
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

        // Firestore 캐싱 활성화 설정
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // 캐싱 활성화
            .build()

        fbsManager.firestoreSettings = settings

        // 캐시에서 정보 가져오기
        fbsManager.collection("posts")
            .whereEqualTo("postId",userUid)
            .get(Source.CACHE)  // 로컬 캐시에서 먼저 데이터 가져오기
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("FeedViewModel", "캐시에 피드 정보가 없습니다")
                    _feedArr.value = null
                } else {
                    Log.d("FeedViewModel", "캐시에서 데이터 가져오기 성공")
                    _feedArr.value = documents.documents.mapNotNull { it.data?.toDataClass<FeedModel>() }.toMutableList()
                }
            }
            .addOnFailureListener { e ->
                Log.d("FeedViewModel", "캐시에서 데이터 불러오기 실패: ${e.message}")
            }

        // 리얼 타임 리스너
        fbsManager.collection("posts")
            .whereEqualTo("postId",userUid)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("FeedViewModel", "리얼타임 데이터 불러오기 실패: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null && !snapshots.isEmpty) {
                    Log.d("FeedViewModel", "리얼타임 데이터 가져오기 성공")
                    _feedArr.value = snapshots.documents.mapNotNull { it.data?.toDataClass<FeedModel>() }.toMutableList()
                } else {
                    Log.d("FeedViewModel", "리얼타임 데이터가 없습니다")
                    _feedArr.value = null
                }
            }


//        // Firebase 서버에서 최신 데이터 가져오기
//        fbsManager.collection("posts")
//            .whereEqualTo("postId",userUid)
//            .get(Source.SERVER)  // 서버에서 최신 데이터 가져오기
//            .addOnSuccessListener { documents ->
//                if (documents.isEmpty) {
//                    Log.d("FeedViewModel", "서버에 피드 정보가 없습니다")
//                } else {
//                    Log.d("FeedViewModel", "서버에서 데이터 가져오기 성공")
//                    _feedArr.value = documents.documents.mapNotNull { it.data?.toDataClass<FeedModel>() }.toMutableList()
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.d("FeedViewModel", "서버에서 데이터 불러오기 실패: ${e.message}")
//            }
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
