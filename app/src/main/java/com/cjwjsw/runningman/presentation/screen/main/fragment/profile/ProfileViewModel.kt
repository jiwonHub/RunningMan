package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fbManager:FirebaseStorage
): ViewModel() {
    private val _photoArr = MutableLiveData<MutableList<Uri>>().apply { value = mutableListOf() }
    val photoArr: LiveData<MutableList<Uri>> get() = _photoArr


    fun upLoadImage(){
        photoArr.value?.forEach {fileUri ->
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val fileReference = fbManager.reference.child("images/$fileName")
            fileReference.putFile(fileUri)
                .addOnSuccessListener {
                    Log.d("upLoadImage",fileUri.toString())
                }
                .addOnFailureListener{
                    Log.d("upLoadImage","failed + $fileUri")
                }
        }
    }

    fun setImageFile(uri : Uri){
        val currentList = _photoArr.value ?: mutableListOf()
        currentList.add(uri)
        _photoArr.value = currentList
        Log.d("ProfileViewModel", uri.toString())
    }

    fun getImageFile(): MutableList<Uri> {
        return _photoArr.value ?: mutableListOf()
    }

}