package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.UserModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fbManager:FirebaseStorage,
    private val fbsManager : FirebaseFirestore
): ViewModel() {
    private val _photoArr = MutableLiveData<MutableList<Uri>>().apply { value = mutableListOf() }
    val photoArr: LiveData<MutableList<Uri>> get() = _photoArr

    private val userUid : UserModel? = UserManager.getInstance()

    private val _uploadStatus = MutableLiveData<Boolean>()
    val uploadStatus: LiveData<Boolean> get() = _uploadStatus

    fun setImageFile(uri : Uri){
        val currentList = _photoArr.value ?: mutableListOf()
        currentList.add(uri)
        _photoArr.value = currentList
        Log.d("ProfileViewModel", uri.toString())
    }
    fun upLoadPost(title : String, content : String){
        val postId = userUid?.uid.toString()
        Log.d("ProfileViewModel",postId)
        val uploadedImageUrls = mutableListOf<String>()
        val imageUris = _photoArr.value ?: return

        imageUris.forEachIndexed { index, imguri ->
            val imageRef = fbManager.reference.child("Post/$postId-$index.jpg")
            imageRef.putFile(imguri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        uploadedImageUrls.add(downloadUrl.toString())
                        if (uploadedImageUrls.size == imageUris.size) {
                            savePostMetadata(postId, title, content, uploadedImageUrls)
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
    // Function to save the post metadata to Firestore
    private fun savePostMetadata(postId: String, title: String, content: String, imageUrls: List<String>) {
        val postMetadata = hashMapOf(
            "postId" to postId,
            "title" to title,
            "content" to content,
            "imageUrls" to imageUrls,
            "timestamp" to FieldValue.serverTimestamp()
        )

        fbsManager.collection("posts").document(postId)
            .set(postMetadata)
            .addOnSuccessListener {
                _uploadStatus.value = true
                _photoArr.value = mutableListOf() // Clear the photo array after successful upload
            }
            .addOnFailureListener { e ->
                _uploadStatus.value = false
                Log.d("ProfileViewModel",e.toString())
            }
    }

    fun getUserFeed(){
        fbsManager.collection("posts").document(userUid?.uid.toString())
            .get()
            .addOnSuccessListener {document ->
                if(document == null){
                    Log.d("ProfileViewModel","해당하는 유저의 저장된 피드는 없음")
                }else{
                    Log.d("ProfileViewModel", document.data.toString())
                }

            }
            .addOnFailureListener {
                Log.d("ProfileViewModel","파이어베이스에서 유저 피드 정보 호출 실패 +${it.toString()}")
            }
    }
}