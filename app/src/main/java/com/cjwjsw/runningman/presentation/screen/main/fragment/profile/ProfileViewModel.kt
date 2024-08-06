package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.FeedModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fbManager:FirebaseStorage,
    private val fbsManager : FirebaseFirestore
): ViewModel() {
    private val _photoArr = MutableLiveData<MutableList<Uri>>().apply { value = mutableListOf() }
    val arr : MutableList<FeedModel> = mutableListOf()
    val photoArr: LiveData<MutableList<Uri>> get() = _photoArr

    private val _feedArr = MutableLiveData<MutableList<FeedModel>?>()
    val feedArr: LiveData<MutableList<FeedModel>?> get() = _feedArr

    private val userUid : String = userData?.idToken.toString()
    private val profileImg : String = userData?.profileUrl.toString()
    private val userName : String = userData?.nickName.toString()

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> get() = _uploadStatus

    fun setImageFile(uri : Uri){
        val currentList = _photoArr.value ?: mutableListOf()
        currentList.add(uri)
        _photoArr.value = currentList
        Log.d("ProfileViewModel", uri.toString())
    }
    fun upLoadPost(title : String, content : String){
        Log.d("ProfileViewModel", userUid)
        val uploadedImageUrls = mutableListOf<String>()
        val imageUris = _photoArr.value ?: return

        imageUris.forEachIndexed { index, imguri ->
            val imageRef = fbManager.reference.child("Post/$userUid-$index.jpg")
            imageRef.putFile(imguri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        uploadedImageUrls.add(downloadUrl.toString())
                        if (uploadedImageUrls.size == imageUris.size) {
                            val feedUID = UUID.randomUUID().toString()
                            savePostMetadata(userUid, title, content, uploadedImageUrls, feedUID, profileImg,userName,0,false)
                        }
                    }.addOnFailureListener { e ->
                        _uploadStatus.value = false
                        Log.d("ProfileViewModel",e.toString())
                    }
                }
                .addOnFailureListener { e ->
                    _uploadStatus.value = false
                    Log.d("ProfileViewModel",e.toString())
                }
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
        likedCount : Int,
        isLiked : Boolean,
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
            "isLiked" to isLiked
        )

        fbsManager.collection("posts").document(feedUID)
            .set(postMetadata)
            .addOnSuccessListener {
                _uploadStatus.value = true
                _photoArr.value = mutableListOf()
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = false
                Log.d("ProfileViewModel",e.toString())
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


    inline fun <reified T> Map<String, Any>.toDataClass(): T {
        val json = JSONObject(this).toString()
        return Gson().fromJson(json, T::class.java)
    }

    companion object{
        val userData = UserManager.getInstance()
    }

}
